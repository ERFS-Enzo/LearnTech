package com.curso.learntech.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI learnTechOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LearnTech API")
                        .description("API didática para cadastro de cursos, categorias "
                                + "e autores, com ajuste de carga horária e consultas paginadas.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Projeto LearnTech")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório do projeto")
                        .url("https://github.com/ERFS-Enzo/LearnTech"));
    }
}