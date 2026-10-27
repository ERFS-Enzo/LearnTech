package com.curso.learntech.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CursoRequest(
        @NotNull(message = "Código do curso é obrigatório")
        @PositiveOrZero(message = "Código do curso não pode ser negativo")
        Integer codigoCurso,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 150, message = "Descrição deve possuir no máximo 150 caracteres")
        String descricao,

        @NotNull(message = "Carga Horária é obrigatória")
        @Positive(message = "Carga Horária deve ser maior que zero")
        BigDecimal cargaHoraria,

        @NotNull(message = "Valor do curso é obrigatório")
        @PositiveOrZero(message = "Valor do curso não pode ser negativo")
        BigDecimal valorCurso,

        @NotNull(message = "Quantidade de módulos é obrigatória")
        @PositiveOrZero(message = "Quantidade de módulos não pode ser negativa")
        Integer quantidadeModulos,

        @NotNull(message = "Categoria é obrigatória")
        @Positive(message = "Identificador da categoria deve ser positivo")
        Long categoriaId,

        @Positive(message = "Identificador do autor deve ser positivo")
        Long autorId
) {
}