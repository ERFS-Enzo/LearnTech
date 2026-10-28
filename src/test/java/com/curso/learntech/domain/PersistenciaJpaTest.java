package com.curso.learntech.domain;

import com.curso.learntech.repository.CategoriaCursoRepository;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class PersistenciaJpaTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CategoriaCursoRepository categoriaRepository;

    @Test
    @Transactional
    void bancoDeveImpedirNomeDeCategoriaDuplicadoIgnorandoMaiusculas() {
        categoriaRepository.saveAndFlush(new CategoriaCurso("Papelaria"));

        assertThrows(
                DataIntegrityViolationException.class,
                () -> categoriaRepository.saveAndFlush(new CategoriaCurso("PAPELARIA")));
    }

    @Test
    @Transactional
    void devePersistirERelerCategoriaECurso() {
        CategoriaCurso categoria = new CategoriaCurso("Backend");
        Curso curso = new Curso(
                1,
                "Aprenda .Net Backend com Asp.Net Core",
                new BigDecimal("50.00"),
                new BigDecimal("89.90"),
                LocalDate.of(2026, 3, 10),
                7,
                Status.ATIVO);

        categoria.adicionarCurso(curso);
        entityManager.persist(categoria);
        entityManager.persist(curso);
        entityManager.flush();

        Long cursoId = curso.getId();
        entityManager.clear();

        Curso recuperado = entityManager.find(Curso.class, cursoId);
        assertEquals("Backend", recuperado.getCategoria().getNome());
    }
}
