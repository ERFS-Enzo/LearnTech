package com.curso.learntech.api.dto;

import com.curso.learntech.domain.StatusInscricao;

import java.time.LocalDateTime;

public record InscricaoResumoResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        Long atendenteId,
        StatusInscricao status,
        LocalDateTime dataAbertura) {
}