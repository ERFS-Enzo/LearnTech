package com.curso.learntech.repository;

import com.curso.learntech.domain.ContaAcesso;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContaAcessoRepository
        extends JpaRepository<ContaAcesso, Long> {

    @EntityGraph(attributePaths = {"pessoa", "perfis"})
    Optional<ContaAcesso> findByLoginIgnoreCase(String login);

    boolean existsByLoginIgnoreCase(String login);

    boolean existsByPessoaId(Long pessoaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaAcesso c where c.id = :id")
    Optional<ContaAcesso> buscarParaAtualizacao(
            @Param("id") Long id);
}