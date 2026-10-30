package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InscricaoRequest(
        @NotNull(message = "Aluno é obrigatório")
        @Positive(message = "Identificador do aluno deve ser positivo")
        Long alunoId,

        @NotNull(message = "Atendente é obrigatório")
        @Positive(message = "Identificador do atendente deve ser positivo")
        Long atendenteId) {
}