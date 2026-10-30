package com.curso.learntech.api.dto;

public record AlunoResponse(
        Long id,
        Long pessoaId,
        String nome,
        String telefone) {
}