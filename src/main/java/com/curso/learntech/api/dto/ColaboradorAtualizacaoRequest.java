package com.curso.learntech.api.dto;

import com.curso.learntech.domain.FuncaoColaborador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ColaboradorAtualizacaoRequest(
        @NotBlank(message = "Matrícula é obrigatória")
        @Size(max = 30, message = "Matrícula deve possuir no máximo 30 caracteres")
        String matricula,

        @NotNull(message = "Função é obrigatória")
        FuncaoColaborador funcao) {
}