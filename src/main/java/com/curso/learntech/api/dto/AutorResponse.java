package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;

public record AutorResponse(
        Long id,
        String nomeAutor,
        String cpf,
        Status status
) {
}
