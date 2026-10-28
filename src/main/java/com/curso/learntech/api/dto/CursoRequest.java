package com.curso.learntech.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastrar um curso")
public record CursoRequest(

        @Schema(description = "Código numérico único do curso", example = "101")
        @NotNull(message = "Código do curso é obrigatório")
        @PositiveOrZero(message = "Código do curso não pode ser negativo")
        Integer codigoCurso,

        @Schema(description = "Descrição do curso", example = "Spring Boot para APIs REST")
        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 150, message = "Descrição deve possuir no máximo 150 caracteres")
        String descricao,

        @Schema(description = "Carga horária total em horas, maior que zero", example = "40.000")
        @NotNull(message = "Carga Horária é obrigatória")
        @Positive(message = "Carga Horária deve ser maior que zero")
        BigDecimal cargaHoraria,

        @Schema(description = "Valor do curso, não negativo", example = "149.90")
        @NotNull(message = "Valor do curso é obrigatório")
        @PositiveOrZero(message = "Valor do curso não pode ser negativo")
        BigDecimal valorCurso,

        @Schema(description = "Quantidade de módulos do curso", example = "8")
        @NotNull(message = "Quantidade de módulos é obrigatória")
        @PositiveOrZero(message = "Quantidade de módulos não pode ser negativa")
        Integer quantidadeModulos,

        @Schema(description = "Identificador de uma categoria existente", example = "1")
        @NotNull(message = "Categoria é obrigatória")
        @Positive(message = "Identificador da categoria deve ser positivo")
        Long categoriaId,

        @Schema(description = "Identificador opcional de um autor existente",
                example = "1", nullable = true)
        @Positive(message = "Identificador do autor deve ser positivo")
        Long autorId
) {
}