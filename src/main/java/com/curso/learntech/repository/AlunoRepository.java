package com.curso.learntech.repository;

import com.curso.learntech.domain.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    boolean existsByPessoaId(Long pessoaId);

    @EntityGraph(attributePaths = "pessoa")
    Optional<Aluno> findOneById(Long id);

    @Override
    @EntityGraph(attributePaths = "pessoa")
    Page<Aluno> findAll(Pageable pageable);
}