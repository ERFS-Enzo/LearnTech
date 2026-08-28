package com.curso.learntech.domain;

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
