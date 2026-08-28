package com.curso.learntech.repository;

import com.curso.learntech.domain.CategoriaCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaCursoRepository extends JpaRepository<CategoriaCurso, Long> {

    boolean existsByNomeIgnoreCase(String nome);
    Optional<CategoriaCurso> findByNomeIgnoreCase(String nome);

}
