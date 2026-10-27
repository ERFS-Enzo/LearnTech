package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AutorRequest(
        @NotBlank(message = "Nome do autor é obrigatório")
        @Size(max = 150, message = "Nome do autor deve possuir no máximo 150 caracteres")
        String nomeAutor,

        @NotBlank(message = "Cpf é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "Cpf deve possuir exatamente 11 números")
        String cpf
) {
}