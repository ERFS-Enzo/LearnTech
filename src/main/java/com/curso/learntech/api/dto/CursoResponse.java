package com.curso.learntech.api.dto;

import com.curso.learntech.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Representação completa de um curso")
public record CursoResponse(
        @Schema(description = "Identificador gerado pelo servidor", example = "1")
        Long id,

        @Schema(description = "Código numérico único do curso", example = "101")
        int codigoCurso,

        @Schema(description = "Descrição do curso", example = "Spring Boot para APIs REST")
        String descricao,

        @Schema(description = "Carga horária total em horas", example = "40.000")
        BigDecimal cargaHoraria,

        @Schema(description = "Valor do curso", example = "149.90")
        BigDecimal valorCurso,

        @Schema(description = "Valor do curso dividido pela carga horária", example = "3.75")
        BigDecimal valorPorHora,

        @Schema(description = "Quantidade de módulos", example = "8")
        int quantidadeModulos,

        @Schema(description = "Data de cadastro definida pelo servidor", example = "2026-10-28")
        LocalDate dataCadastro,

        @Schema(description = "Situação do curso", example = "ATIVO")
        Status status,

        @Schema(description = "Identificador da categoria", example = "1")
        Long categoriaId,

        @Schema(description = "Nome da categoria", example = "Programação")
        String categoriaNome,

        @Schema(description = "Identificador do autor, quando houver", example = "1", nullable = true)
        Long autorId,

        @Schema(description = "Nome do autor, quando houver", example = "Maria Souza", nullable = true)
        String autorNome
) {
}