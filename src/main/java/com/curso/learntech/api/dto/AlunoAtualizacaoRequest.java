package com.curso.learntech.api.dto;

import jakarta.validation.constraints.Size;

public record AlunoAtualizacaoRequest(
        @Size(max = 30, message = "Telefone deve possuir no máximo 30 caracteres")
        String telefone) {
}