package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AutorRequest(
        @NotNull(message = "Autor é obrigatório")
        @Positive(message = "Identificador do Autor deve ser positivo")
        Long Id,

        @NotBlank(message = "Nome do autor é obrigatória")
        @Size(max = 150, message = "Nome do autor deve possuir no máximo 150 caracteres")
        String nomeAutor,

        @NotBlank(message = "Cpf é obrigatório")
        @Size(max = 11, message = "Cpf deve possuir no máximo 11 caracteres")
        String cpf
) {
}
