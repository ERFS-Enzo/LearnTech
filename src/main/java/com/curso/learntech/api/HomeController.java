package com.curso.learntech.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Infraestrutura", description = "Verificações técnicas da aplicação")
@RestController
public class HomeController {

    @Operation(summary = "Mensagem de boas-vindas",
            description = "Endpoint auxiliar para testar rapidamente a aplicação.")
    @GetMapping("/api/home")
    public String home() {
        return "Bem vindo ao Home!";
    }
}