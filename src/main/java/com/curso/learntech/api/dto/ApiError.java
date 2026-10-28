package com.curso.learntech.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Representação padronizada de um erro da API")
public record ApiError(
        @Schema(example = "2026-10-28T18:30:00Z")
        Instant timestamp,

        @Schema(description = "Código HTTP numérico", example = "400")
        int status,

        @Schema(description = "Nome padronizado do status HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensagem segura para o consumidor")
        String message,

        @Schema(example = "/api/cursos")
        String path,

        @Schema(description = "Erros separados por campo, quando houver")
        Map<String, String> fields) {
}