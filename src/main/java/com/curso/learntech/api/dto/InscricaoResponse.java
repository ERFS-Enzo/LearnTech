package com.curso.learntech.api.dto;

import com.curso.learntech.domain.StatusInscricao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InscricaoResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        Long atendenteId,
        String atendenteNome,
        StatusInscricao status,
        LocalDateTime dataAbertura,
        LocalDateTime dataFinalizacao,
        BigDecimal total,
        List<ItemInscricaoResponse> itens) {
}