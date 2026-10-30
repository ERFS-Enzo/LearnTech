package com.curso.learntech.api.dto;

import com.curso.learntech.domain.FuncaoColaborador;

import java.time.LocalDate;

public record ColaboradorResponse(
        Long id,
        Long pessoaId,
        String nome,
        String matricula,
        FuncaoColaborador funcao,
        LocalDate dataAdmissao) {
}