package com.curso.learntech.api.dto;

import java.math.BigDecimal;

public record ItemInscricaoResponse(
        Long id,
        Long cursoId,
        int codigoCurso,
        String descricao,
        BigDecimal valorPraticado) {
}