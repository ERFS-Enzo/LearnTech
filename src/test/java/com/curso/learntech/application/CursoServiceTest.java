package com.curso.learntech.application;

import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.CategoriaCursoRepository;
import com.curso.learntech.repository.CursoRepository;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CursoServiceTest {
    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private CategoriaCursoRepository categoriaRepository;

    @Test
    void deveCadastrarCursoComCategoria() {
        CategoriaCurso categoria = categoriaRepository.save(
                new CategoriaCurso("backend"));

        Curso cadastrado = cursoService.cadastrar(
                novoCurso(999),
                categoria.getId());

        assertNotNull(cadastrado.getId());
        assertEquals(categoria.getId(), cadastrado.getCategoria().getId());
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> cursoService.cadastrar(novoCurso(1), Long.MAX_VALUE));

        assertFalse(cursoRepository.existsByCodigoCurso(novoCurso(1).getCodigoCurso()));
    }

    private Curso novoCurso(int codigoCurso) {
        return new Curso(
                codigoCurso,
                "C# e .NET para Iniciantes",
                new java.math.BigDecimal("40"),
                new java.math.BigDecimal("149.90"),
                java.time.LocalDate.of(2026, 8, 20),
                Status.ATIVO);
    }
}

