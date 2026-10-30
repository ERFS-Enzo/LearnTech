package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;

import java.time.LocalDate;

public record PessoaResponse(
        Long id,
        String nome,
        String email,
        String cpf,
        LocalDate dataCadastro,
        Status status) {
}