package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Novo status de um recurso")
public record StatusRequest(
        @Schema(description = "ATIVO ou INATIVO", example = "INATIVO")
        @NotNull(message = "Status é obrigatório")
        Status status) {
}