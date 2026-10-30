package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AlunoRequest(
        @NotNull(message = "Pessoa é obrigatória")
        @Positive(message = "Identificador da pessoa deve ser positivo")
        Long pessoaId,

        @Size(max = 30, message = "Telefone deve possuir no máximo 30 caracteres")
        String telefone) {
}