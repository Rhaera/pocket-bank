package com.github.rhaera.project.pocketbank.controller.web.view;

import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;
import com.github.rhaera.project.pocketbank.repository.ClientDetailRepository;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class ViewController {
    private final ClientDetailRepository repository; // criar camada de service

    @GetMapping(value = {"/", "home"})
    public String hello(Model model) {
        model.addAttribute("title", "POCKET BANK");
        model.addAttribute("message", "Welcome To...A Bank In A Nutshell!!!");
        model.addAttribute("greetings", "Come with me! Let's explore this AMAZING WEBAPP!!!");
        model.addAttribute("login", "Login to your account...");
        model.addAttribute("signup", "Create Account...");
        return "home";
    }

    @GetMapping(value = "admin")
    public String adminArea(Model model) {
        model.addAttribute("title", "Hi Admin! Let's Review POCKET BANK API DOCS!!!");
        model.addAttribute("loans", "Come on! Let's check you the client's loan offer!");
        model.addAttribute("insurances", "It's Showtime!!! Invite them for POCKET INSURANCE!");
        return "admin";
    }

    @GetMapping(value = "user/login")
    public String clientLogin(Model model) {
        return "login";
    }

    @GetMapping(value = "user/register")
    public String clientRegisterForm(Model model) {
        model.addAttribute("user", new ClientDTO());
        return "signup";
    }

    @PostMapping(value = "user/register")
    public RedirectView clientRegistryRequest(RedirectAttributes redirectAttributes, @ModelAttribute("user") @Valid ClientDTO newClientRequest) throws IOException {
        if (repository.findByCpf(newClientRequest.getCpf()).isPresent() || UtilLocalizacao.agenciaMaisProxima(newClientRequest.getCep()).equals("0000")) {
            redirectAttributes.addFlashAttribute("cepErrado", newClientRequest.getCep());
            return new RedirectView("/user/zip-error");
        }
        return new RedirectView(("/clients/client-detail/").concat(newClientRequest.toString()));
    }

    @GetMapping(value = "/user/zip-error")
    public String zipCodeError(Model model, @ModelAttribute("cepErrado") String cep) {
        model.addAttribute("content", "BAD REQUEST " + cep);
        return "zip-error";
    }
}
