package com.curso.learntech.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaCursoAtualizacaoRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 120, message = "Nome deve possuir no máximo 120 caracteres")
        String nome) {
}