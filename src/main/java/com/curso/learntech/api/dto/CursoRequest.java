package com.curso.learntech.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CursoRequest(
        @NotNull(message = "Código do curso é obrigatório")
        @Size(max = 50, message = "Código do curso deve possuir no máximo 50 caracteres")
        int codigoCurso,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 150, message = "Descrição deve possuir no máximo 150 caracteres")
        String descricao,

        @NotNull(message = "Carga Horária é obrigatória")
        @PositiveOrZero(message = "Carga Horária não pode ser negativo")
        BigDecimal cargaHoraria,

        @NotNull(message = "Valor do curo é obrigatório")
        @PositiveOrZero(message = "Valor do curso não pode ser negativo")
        BigDecimal valorCurso,

        @NotNull(message = "Quantidade de módulos é obrigatório")
        @PositiveOrZero(message = "Quantidade de módulos não pode ser negativo")
        int quantidadeModulos,

        @NotNull(message = "Categoria é obrigatório")
        @Positive(message = "Identificador da categoria deve ser positivo")
        Long categoriaId,

        @Positive(message = "Identificador do autor deve ser positivo")
        Long autorId
) {
}
