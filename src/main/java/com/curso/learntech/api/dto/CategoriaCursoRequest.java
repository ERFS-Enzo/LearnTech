package com.curso.learntech.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastrar uma categoria de cursos")
public record CategoriaCursoRequest(
        @Schema(description = "Nome único da categoria, sem diferenciar maiúsculas",
                example = "Programação")
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres")
        String nome
) {
}