package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CursoResponse(
        Long id,
        int codigoCurso,
        String descricao,
        BigDecimal cargaHoraria,
        BigDecimal valorCurso,
        BigDecimal quantidadeModulos,
        LocalDate dataCadastro,
        Status status,
        Long categoriaId,
        String categoriaNome,
        Long autorId,
        String autorNome

) {
}
