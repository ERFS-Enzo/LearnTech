package com.curso.learntech.config;

import com.curso.learntech.application.ContaAcessoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AdministradorInicialRunner implements CommandLineRunner {

    private final ContaAcessoService service;
    private final Environment environment;

    public AdministradorInicialRunner(
            ContaAcessoService service,
            Environment environment) {

        this.service = service;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        boolean habilitado = environment.getProperty(
                "app.admin-inicial.enabled",
                Boolean.class,
                false);

        if (!habilitado) {
            return;
        }

        service.inicializarAdministrador(
                obrigatorio("app.admin-inicial.nome"),
                obrigatorio("app.admin-inicial.email"),
                obrigatorio("app.admin-inicial.cpf"),
                obrigatorio("app.admin-inicial.login"),
                obrigatorio("app.admin-inicial.senha"));
    }

    private String obrigatorio(String propriedade) {
        String valor = environment.getProperty(propriedade);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Configure " + propriedade);
        }

        return valor;
    }
}