package com.github.rhaera.project.pocketbank.controller.web.view;

import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;
import com.github.rhaera.project.pocketbank.service.JwtTokenService;
import com.github.rhaera.project.pocketbank.service.implementation.ClientService;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;

@Controller
@RequestMapping("/clients/")
@Slf4j
@RequiredArgsConstructor
public class ClientViewController {
    private final ClientService clientService;
    private final JwtTokenService jwtService;
    private final JavaMailSender mailSender;

    private void emailValidation(String url, Instant instant) throws MessagingException, UnsupportedEncodingException, FileNotFoundException {
        ClientDTO dto = new ClientDTO().reverseToDto(url);
        MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage());
        helper.setFrom("noreply@pocket-bank.com", "Pocket Bank");
        helper.setTo(dto.getEmail());
        helper.setSubject("Validar Email para Cadastro da PockeConta");
        helper.setText("<p>OLÁ, " + dto.getName().toUpperCase() + ",</p>" +
                        "<p>obrigado pelo seu interesse em criar uma PockeConta.</p>" +
                        "<p>Por favor, use o código abaixo para finalizar seu cadastro:</p>" +
                        "<a href=/clients/client-validation/" + url + ">VERIFIQUE SEU EMAIL E FINALIZE SEU CADASTRO!</a><br>" +
                        "<h2 id='link' name='link'><b>" +
                        jwtService.getToken(UsernamePasswordAuthenticationToken.unauthenticated(dto.toEntity().getUsername(), dto.getSenha()), instant) +
                        "</b></h2>" +
                        "<button onClick='copyToken()'>Copiar Token</button>" +
                        "<script>" +
                            "function copyToken() {" +
                                "var tokenValue = document.getElementById('link');" +
                                "tokenValue.select();" +
                                "document.execCommand('Copy');" +
                                "alert('Texto Copiado: ' + tokenValue.value);" +
                            "}" +
                        "</script>", true);
        mailSender.send(helper.getMimeMessage());
    }

    @GetMapping(value = "client-detail/{dto}")
    public ModelAndView clientGetRevenue(RedirectAttributes redirectAttributes, @PathVariable("dto") String dto) {
        System.out.println(dto);
        System.out.println(new ClientDTO().reverseToDto(dto).getEmail());
        redirectAttributes.addFlashAttribute("job", "")
                        .addFlashAttribute("revenue", "");
        return new ModelAndView("revenue");
    }

    @PostMapping(value = "client-detail/{dto}")
    public RedirectView clientSendDetails(@PathVariable("dto") String dto, @ModelAttribute("job") String job, @ModelAttribute("revenue") String revenue) {
        Instant i = Instant.now();
        log.info(String.valueOf(i.toString().equals(Instant.parse(i.toString()).toString()))); // instant test
        return new RedirectView("/clients/" + dto + "/details?job=" + job + "&revenue=" + revenue); // ?id=repo.findById(newClient)
    }

    @GetMapping(value = "{dto}/details")
    public ModelAndView clientSendValidation(@PathVariable("dto") String dto, @RequestParam("job") String job, @RequestParam("revenue") String revenue)
            throws MessagingException, UnsupportedEncodingException {
        System.out.println(job+revenue);
        //emailValidation(url);
        return new ModelAndView("client-validator", "send", true);
    }

    @PostMapping(value = "{dto}/details")
    public RedirectView clientValidationChecked(RedirectAttributes attributes) {
        attributes.addFlashAttribute("send", false);
        return new RedirectView("/clients/area");
    }

    @GetMapping(value = "area")
    public String clientArea(Model model) {
        model.addAttribute("title", "Your Pocket Account");
        model.addAttribute("loans", "Come on! Let's see if you have a loan offer!");
        model.addAttribute("insurances", "Do you wanna POCKET INSURANCE?!");
        return "client-validator";
    }
}
