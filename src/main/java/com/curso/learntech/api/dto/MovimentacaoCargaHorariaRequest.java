package com.curso.learntech.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Quantidade de horas a acrescentar ou reduzir")
public record MovimentacaoCargaHorariaRequest(
        @Schema(description = "Horas, maior que zero", example = "5.5")
        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        BigDecimal quantidade) {
}