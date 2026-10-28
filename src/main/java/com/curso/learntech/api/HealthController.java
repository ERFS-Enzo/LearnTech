package com.curso.learntech.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Infraestrutura", description = "Verificações técnicas da aplicação")
@RestController
public class HealthController {

    @Operation(summary = "Verificar se a aplicação está no ar",
            description = "Devolve o texto OK quando a aplicação está respondendo.")
    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }
}