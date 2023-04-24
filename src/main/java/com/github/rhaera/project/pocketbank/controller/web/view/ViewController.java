package com.github.rhaera.project.pocketbank.controller.web.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ViewController {

    @GetMapping(value = {"/", "/home"})
    public String hello(Model model) {
        model.addAttribute("title", "POCKET BANK");
        model.addAttribute("message", "Welcome To...A Bank In A Nutshell!!!");
        model.addAttribute("thanks", "Come with me! Let's explore this AMAZING WEBAPP!!!");
        return "home";
    }
}
