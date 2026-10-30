package com.curso.learntech.api.dto;

import com.curso.learntech.domain.FuncaoColaborador;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ColaboradorRequest(
        @NotNull(message = "Pessoa é obrigatória")
        @Positive(message = "Identificador da pessoa deve ser positivo")
        Long pessoaId,

        @NotBlank(message = "Matrícula é obrigatória")
        @Size(max = 30, message = "Matrícula deve possuir no máximo 30 caracteres")
        String matricula,

        @NotNull(message = "Função é obrigatória")
        FuncaoColaborador funcao,

        @NotNull(message = "Data de admissão é obrigatória")
        LocalDate dataAdmissao) {
}