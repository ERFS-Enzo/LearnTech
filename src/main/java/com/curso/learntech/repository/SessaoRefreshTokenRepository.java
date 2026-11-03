package com.curso.learntech.repository;

import com.curso.learntech.domain.SessaoRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SessaoRefreshTokenRepository
        extends JpaRepository<SessaoRefreshToken, Long> {

    @Query("""
            select s.conta.id
            from SessaoRefreshToken s
            where s.tokenHash = :hash
            """)
    Optional<Long> buscarContaId(@Param("hash") String hash);

    Optional<SessaoRefreshToken> findByTokenHash(String hash);

    @Modifying(flushAutomatically = true)
    @Query("""
            update SessaoRefreshToken s
            set s.revogada = true
            where s.familia = :familia
            """)
    int revogarFamilia(@Param("familia") UUID familia);

    @Modifying(flushAutomatically = true)
    @Query("""
            update SessaoRefreshToken s
            set s.revogada = true
            where s.conta.id = :contaId
            """)
    int revogarTodas(@Param("contaId") Long contaId);
}