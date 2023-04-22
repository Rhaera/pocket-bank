package com.github.rhaera.project.pocketbank.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/")
public class ViewController {

    @GetMapping(value = {"/", "/home"})
    public String hello(Model model) {
        String t = "GLÓRYAH ETERNA";
        String m = "YAH!";
        String p = "ALL GLORY TO THE ETERNAL FATHER";
        model.addAttribute("title", t);
        model.addAttribute("message", m);
        model.addAttribute("thanks", p);
        return "home";
    }

}
