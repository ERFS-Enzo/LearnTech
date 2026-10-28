package com.curso.learntech.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar um autor")
public record AutorRequest(
        @Schema(description = "Nome do autor", example = "Maria Souza")
        @NotBlank(message = "Nome do autor é obrigatório")
        @Size(max = 150, message = "Nome do autor deve possuir no máximo 150 caracteres")
        String nomeAutor,

        @Schema(description = "CPF fictício com 11 números, sem pontos ou traço",
                example = "12345678901")
        @NotBlank(message = "Cpf é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "Cpf deve possuir exatamente 11 números")
        String cpf
) {
}