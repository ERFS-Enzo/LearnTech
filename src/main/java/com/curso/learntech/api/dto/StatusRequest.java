package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "Status é obrigatório")
        Status status) {
}