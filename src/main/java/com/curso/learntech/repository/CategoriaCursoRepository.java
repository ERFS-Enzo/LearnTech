package com.curso.learntech.repository;

import com.curso.learntech.domain.CategoriaCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoriaCursoRepository extends
        JpaRepository<CategoriaCurso, Long>,
        JpaSpecificationExecutor<CategoriaCurso> {

    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
    Optional<CategoriaCurso> findByNomeIgnoreCase(String nome);
}