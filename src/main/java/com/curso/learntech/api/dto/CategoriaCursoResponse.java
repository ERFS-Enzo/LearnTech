package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representação de uma categoria de cursos")
public record CategoriaCursoResponse(
        @Schema(description = "Identificador gerado pelo servidor", example = "1")
        Long id,

        @Schema(description = "Nome da categoria", example = "Programação")
        String nome,

        @Schema(description = "Situação da categoria", example = "ATIVO")
        Status status
) {
}