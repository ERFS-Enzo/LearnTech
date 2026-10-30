package com.curso.learntech.repository;

import com.curso.learntech.domain.Colaborador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    boolean existsByPessoaId(Long pessoaId);
    boolean existsByMatricula(String matricula);
    boolean existsByMatriculaAndIdNot(String matricula, Long id);

    @EntityGraph(attributePaths = "pessoa")
    Optional<Colaborador> findOneById(Long id);

    @Override
    @EntityGraph(attributePaths = "pessoa")
    Page<Colaborador> findAll(Pageable pageable);
}