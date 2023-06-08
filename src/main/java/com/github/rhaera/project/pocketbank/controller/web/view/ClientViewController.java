package com.github.rhaera.project.pocketbank.controller.web.view;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.ClientForm;
import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;
import com.github.rhaera.project.pocketbank.model.utility.UtilFormatacoes;
import com.github.rhaera.project.pocketbank.service.implementation.ClientDetailService;
import com.github.rhaera.project.pocketbank.service.implementation.ClientService;
import com.github.rhaera.project.pocketbank.service.JwtTokenService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/clients/")
@Slf4j
public class ClientViewController {
    private final ClientDetailService clientDetailService;
    private final ClientService clientService;
    private final JwtTokenService jwtService;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ClientViewController(@Lazy ClientDetailService clientDetailService, ClientService clientService, JwtTokenService jwtService) {
        this.clientDetailService = clientDetailService;
        this.clientService = clientService;
        this.jwtService = jwtService;
    }

    @SuppressWarnings(value = "unchecked")
    private Map<String, Object> getCredentials() {
        return (Map<String, Object>) clientDetailService.getCredentials();
    }

    private JavaMailSender getMailSender() {
        return clientDetailService.getMailSender();
    }

    private boolean tokenTimeVerification(HttpServletRequest request) {
        return !request.getRequestURI().equals("/clients/validator") &&
                jwtService.getKeyCreationTime() > 0 &&
                Instant.parse(jwtService.getKey())
                        .plus(5, ChronoUnit.MINUTES)
                        .isAfter(Instant.now());
    }

    private void emailValidation(String url, String token) throws MessagingException, UnsupportedEncodingException, FileNotFoundException {
        ClientDTO dto = new ClientDTO().reverseToDto(url);
        MimeMessageHelper helper = new MimeMessageHelper(getMailSender().createMimeMessage());
        helper.setFrom("rhaera29@gmail.com", "Pocket Bank");
        helper.setTo(dto.getEmail());
        helper.setSubject("Validar Email para Cadastro da PockeConta");
        helper.setText("<p>OLÁ, " + dto.getName().toUpperCase() + ",</p>" +
                        "<p>obrigado pelo seu interesse em criar uma PockeConta.</p>" +
                        "<p>Por favor, use o código abaixo para finalizar seu cadastro:</p>" +
                        "<a href='http://localhost:8080/clients/validator'>VERIFIQUE SEU EMAIL E FINALIZE SEU CADASTRO!</a><br>" +
                        "<h2 id='link' name='link'><b>" +
                        token +
                        "</b></h2>", true);
        getMailSender().send(helper.getMimeMessage());
    }

    @GetMapping(value = "client-detail/{dto}")
    public ModelAndView clientGetRevenue(RedirectAttributes redirectAttributes, @PathVariable("dto") String dto) {
        log.info(dto);
        log.info(new ClientDTO().reverseToDto(dto).getEmail());
        redirectAttributes.addFlashAttribute("job", "")
                        .addFlashAttribute("revenue", "");
        return new ModelAndView("revenue");
    }

    @PostMapping(value = "client-detail/{dto}")
    public RedirectView clientSendDetails(@PathVariable("dto") String dto, @ModelAttribute("job") String job, @ModelAttribute("revenue") String revenue) {
        Instant i = Instant.now();
        log.info(String.valueOf(i.toString().equals(Instant.parse(i.toString()).toString())));
        return new RedirectView("/clients/" + dto + "/details?job=" + job + "&revenue=" + revenue);
    }

    @GetMapping(value = "{dto}/details")
    public ModelAndView clientSendValidation() {
        return new ModelAndView("client-validator", "send", true).addObject("alert", false)
                                                                                                    .addObject("response", "");
    }

    @PostMapping(value = {"{dto}/details", "validator"})
    public RedirectView clientValidationSent(RedirectAttributes attributes, HttpServletRequest request, @PathVariable(value = "dto", required = false) String dto)
            throws MessagingException, IOException {
        if (tokenTimeVerification(request)) {
            return new RedirectView("/clients/validator".concat("?id=".concat(new ClientDTO().reverseToDto(dto)
                                                        .getCpf())
                                                        .concat("-".concat(jwtService.getKey()))));
        }
        if (!request.getRequestURI().equals("/clients/validator")) {
            attributes.addFlashAttribute("send", false)
                        .addFlashAttribute("save", dto)
                        .addFlashAttribute("params", request.getParameterMap()
                                                                        .values()
                                                                        .stream()
                                                                        .map(param -> "**".concat(Arrays.toString(param)
                                                                        .replace("[", "")
                                                                        .replace("]", "")))
                                                                        .reduce("", String::concat));
            getCredentials().put("principal", dto);
            getCredentials().put("details", attributes.getFlashAttributes()
                            .get("params"));
            String token = jwtService.getToken(clientDetailService);
            attributes.addFlashAttribute("response", token);
            getCredentials().put("token", token);
            emailValidation(dto, token);
            log.info(token);
            return new RedirectView("/clients/validator".concat("?id=".concat(new ClientDTO().reverseToDto(dto)
                                                        .getCpf())
                                                        .concat("-".concat(jwtService.getKey()))));
        }
        ClientDTO newDTO = new ClientDTO().reverseToDto((String) clientDetailService.getPrincipal());
        if (clientService.postClient(new ClientForm(newDTO.getCpf(),
                                                    UtilFormatacoes.formatarDataNascimento(newDTO.getDataNascimento()),
                                                    Long.parseLong(newDTO.getCep()))).isPresent())
            clientDetailService.createUser(new User(newDTO.getName(), newDTO.getSenha(), clientDetailService.getAuthorities()));
        if ((Boolean) getCredentials().get("error")) {
            attributes.addFlashAttribute("alert", true);
            getCredentials().remove("error");
            executorService.execute(() -> {
                try {
                    TimeUnit.MILLISECONDS
                            .sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return new RedirectView("/clients/validator".concat("?id=".concat(newDTO.getCpf())
                                                        .concat("-".concat(jwtService.getKey()))));
        }
        return new RedirectView("/clients/area".concat("?client=".concat(new ClientDTO().reverseToDto((String) getCredentials().get("principal"))
                                                                                        .toEntity()
                                                                                        .getUsername()
                                                                                        .replace("@", "-")
                                                                                        .concat("&pin=".concat(UUID.randomUUID()
                                                                                        .toString()
                                                                                        .substring(0, 5))))));
    }

    @GetMapping(value = "validator")
    public ModelAndView clientValidator(@RequestParam(value = "id", required = false) String id) { // @ModelAttribute("response") String token, @ModelAttribute("save") String dto, @ModelAttribute("params") String details,) {
        // if (Objects.nonNull(details) && details.contains("**") && Objects.nonNull(dto)) {
        //     getCredentials().put("token", token);
        //     log.info(dto + "-" + details.substring(details.indexOf("**") + 2, details.substring(details.indexOf("**") + 2).indexOf("**") + 2));
        // }
        // log.info(dto + "-" + details + "-" + token);
        // log.info(clientDetailService.getPrincipal().toString());
        // log.info(clientDetailService.getDetails().toString());
        if (Objects.isNull(id)) return new ModelAndView("client-validator", "response", getCredentials().get("token"));
        return new ModelAndView("client-validator").addObject("alert", false);
    }

    @GetMapping(value = "area")
    public String clientArea(Model model, @RequestParam("client") String username) {
        model.addAttribute("title", "Your Pocket Account");
        model.addAttribute("loans", "Come on! Let's see if you have a loan offer!");
        model.addAttribute("insurances", "Do you wanna POCKET INSURANCE?!");
        return "client-area";
    }
}
