package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record VagasRequest(
        @NotNull(message = "Vagas é obrigatório")
        @PositiveOrZero(message = "Vagas não podem ser negativas")
        Integer vagas) {
}