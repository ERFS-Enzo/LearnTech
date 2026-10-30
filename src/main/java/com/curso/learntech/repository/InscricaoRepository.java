package com.curso.learntech.repository;

import com.curso.learntech.domain.Inscricao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InscricaoRepository extends
        JpaRepository<Inscricao, Long>,
        JpaSpecificationExecutor<Inscricao> {

    @EntityGraph(attributePaths = {"aluno.pessoa", "atendente.pessoa", "itens.curso"})
    Optional<Inscricao> findOneById(Long id);

    // Na pesquisa não carregamos a coleção de itens: a listagem devolve um resumo.
    @Override
    @EntityGraph(attributePaths = {"aluno.pessoa", "atendente.pessoa"})
    Page<Inscricao> findAll(Specification<Inscricao> specification, Pageable pageable);
}