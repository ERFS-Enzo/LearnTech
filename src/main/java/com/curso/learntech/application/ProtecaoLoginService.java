package com.curso.learntech.application;

import com.curso.learntech.domain.ContaAcesso;
import com.curso.learntech.domain.ProtecaoLogin;
import com.curso.learntech.repository.ContaAcessoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.HexFormat;

@Service
public class ProtecaoLoginService {

    private final EntityManager entityManager;
    private final ContaAcessoRepository contaRepository;
    private final AuditoriaService auditoria;
    private final Clock clock;
    private final int limite;
    private final Duration janela;
    private final Duration bloqueio;

    public ProtecaoLoginService(
            EntityManager entityManager,
            ContaAcessoRepository contaRepository,
            AuditoriaService auditoria,
            Clock clock,
            @Value("${app.security.login.max-failures}") int limite,
            @Value("${app.security.login.failure-window}") Duration janela,
            @Value("${app.security.login.lock-duration}") Duration bloqueio) {

        if (limite < 1
                || janela.isZero()
                || janela.isNegative()
                || bloqueio.isZero()
                || bloqueio.isNegative()) {

            throw new IllegalArgumentException(
                    "Política de login inválida");
        }

        this.entityManager = entityManager;
        this.contaRepository = contaRepository;
        this.auditoria = auditoria;
        this.clock = clock;
        this.limite = limite;
        this.janela = janela;
        this.bloqueio = bloqueio;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public ProtecaoLogin bloquearRegistro(String login) {
        String chave = chave(login);

        entityManager.createNativeQuery("""
                INSERT INTO protecao_login (chave, falhas)
                VALUES (:chave, 0)
                ON CONFLICT (chave) DO NOTHING
                """)
                .setParameter("chave", chave)
                .executeUpdate();

        return entityManager.find(
                ProtecaoLogin.class,
                chave,
                LockModeType.PESSIMISTIC_WRITE);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public boolean permitir(
            ProtecaoLogin protecao,
            String login,
            String origem) {

        if (!protecao.estaBloqueada(clock.instant())) {
            return true;
        }

        registrar("LOGIN_BLOQUEADO", login, origem);
        return false;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void falha(
            ProtecaoLogin protecao,
            String login,
            String origem) {

        boolean bloqueou = protecao.registrarFalha(
                clock.instant(),
                limite,
                janela,
                bloqueio);

        registrar("LOGIN_FALHA", login, origem);

        if (bloqueou) {
            registrar("BLOQUEIO_ATIVADO", login, origem);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void sucesso(
            ProtecaoLogin protecao,
            String login,
            String origem) {

        protecao.registrarSucesso();
        registrar("LOGIN_SUCESSO", login, origem);
    }

    private void registrar(
            String tipo,
            String login,
            String origem) {

        Long contaId = contaRepository.findByLoginIgnoreCase(login)
                .map(conta -> conta.getId())
                .orElse(null);

        // Tentativa com senha errada não comprova a identidade do ator.
        Long atorId = tipo.equals("LOGIN_SUCESSO")
                ? contaId
                : null;

        auditoria.registrar(
                tipo,
                atorId,
                contaId,
                chave(login),
                origem);
    }

    private String chave(String login) {
        String normalizado = ContaAcesso.normalizarLogin(login);

        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(normalizado.getBytes(
                                    StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 indisponível", exception);
        }
    }
}