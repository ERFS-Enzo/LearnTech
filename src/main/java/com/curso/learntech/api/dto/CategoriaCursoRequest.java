package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CategoriaCursoRequest(
        @NotNull(message = "Id de categoria é obrigatório")
        @Positive(message = "Identificador deve ser positivo")
        Long Id,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres")
        String nome,

        @NotNull(message = "Curso é obrigatório")
        @Positive(message = "Identificador do curso deve ser positivo")
        Long cursoId

) {
}
