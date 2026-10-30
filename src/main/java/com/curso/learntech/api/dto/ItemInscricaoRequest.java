package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemInscricaoRequest(
        @NotNull(message = "Curso é obrigatório")
        @Positive(message = "Identificador do curso deve ser positivo")
        Long cursoId) {
}