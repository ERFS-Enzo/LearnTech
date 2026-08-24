package com.curso.learntech.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api/home")
    public String home(){
        return "Bem vindo ao Home!";
    }
}
