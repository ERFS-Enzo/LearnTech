package com.curso.learntech.application;

import com.curso.learntech.api.dto.AutenticacaoDtos.TokenResponse;
import com.curso.learntech.domain.ContaAcesso;
import com.curso.learntech.domain.SessaoRefreshToken;
import com.curso.learntech.repository.ContaAcessoRepository;
import com.curso.learntech.repository.SessaoRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class SessaoAutenticacaoService {

    private final ContaAcessoRepository contaRepository;
    private final SessaoRefreshTokenRepository repository;
    private final TokenService tokenService;
    private final Clock clock;
    private final Duration idleTimeout;
    private final Duration absoluteTimeout;
    private final SecureRandom random = new SecureRandom();

    public SessaoAutenticacaoService(
            ContaAcessoRepository contaRepository,
            SessaoRefreshTokenRepository repository,
            TokenService tokenService,
            Clock clock,
            @Value("${app.security.refresh.idle-timeout}") Duration idleTimeout,
            @Value("${app.security.refresh.absolute-timeout}") Duration absoluteTimeout) {

        if (idleTimeout.isZero() || idleTimeout.isNegative()
                || absoluteTimeout.isZero()
                || absoluteTimeout.isNegative()) {

            throw new IllegalArgumentException(
                    "Tempos de sessão devem ser positivos");
        }

        this.contaRepository = contaRepository;
        this.repository = repository;
        this.tokenService = tokenService;
        this.clock = clock;
        this.idleTimeout = idleTimeout;
        this.absoluteTimeout = absoluteTimeout;
    }

    @Transactional
    public TokenResponse iniciar(Authentication authentication) {
        Long contaId = contaRepository.findByLoginIgnoreCase(
                        authentication.getName())
                .orElseThrow(CredenciaisInvalidasException::new)
                .getId();

        ContaAcesso conta = bloquearConta(contaId);

        if (!conta.podeAutenticar()) {
            throw new CredenciaisInvalidasException();
        }

        Instant agora = clock.instant();

        return emitir(
                conta,
                UUID.randomUUID(),
                agora.plus(absoluteTimeout));
    }

    @Transactional(noRollbackFor = CredenciaisInvalidasException.class)
    public TokenResponse renovar(String refreshToken) {
        String hash = hash(refreshToken);

        Long contaId = repository.buscarContaId(hash)
                .orElseThrow(CredenciaisInvalidasException::new);

        // Login, refresh, logout e logout global usam o mesmo lock.
        ContaAcesso conta = bloquearConta(contaId);

        SessaoRefreshToken sessao = repository.findByTokenHash(hash)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!conta.podeAutenticar()
                || !sessao.podeRenovar(clock.instant())) {

            repository.revogarFamilia(sessao.getFamilia());

            // A revogação precisa ser confirmada mesmo retornando 401.
            throw new CredenciaisInvalidasException();
        }

        sessao.marcarUsada();

        return emitir(
                conta,
                sessao.getFamilia(),
                sessao.getExpiracaoAbsoluta());
    }

    @Transactional
    public void encerrar(String refreshToken) {
        String hash = hash(refreshToken);

        var contaId = repository.buscarContaId(hash);

        if (contaId.isEmpty()) {
            return;
        }

        bloquearConta(contaId.get());

        repository.findByTokenHash(hash)
                .ifPresent(sessao ->
                        repository.revogarFamilia(
                                sessao.getFamilia()));
    }

    @Transactional
    public void encerrarTodas(String login) {
        Long id = contaRepository.findByLoginIgnoreCase(login)
                .orElseThrow(CredenciaisInvalidasException::new)
                .getId();

        bloquearConta(id);
        repository.revogarTodas(id);
    }

    private TokenResponse emitir(
            ContaAcesso conta,
            UUID familia,
            Instant expiracaoAbsoluta) {

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String refreshToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        Instant expiraEm = clock.instant().plus(idleTimeout);

        if (expiraEm.isAfter(expiracaoAbsoluta)) {
            expiraEm = expiracaoAbsoluta;
        }

        repository.save(new SessaoRefreshToken(
                conta,
                familia,
                hash(refreshToken),
                expiraEm,
                expiracaoAbsoluta));

        var authorities = conta.getPerfis().stream()
                .map(perfil -> new SimpleGrantedAuthority(
                        "ROLE_" + perfil.name()))
                .toList();

        var authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        conta.getLogin(),
                        null,
                        authorities);

        var accessToken =
                tokenService.emitir(authentication, familia);

        return new TokenResponse(
                accessToken.valor(),
                "Bearer",
                accessToken.expiresIn(),
                refreshToken);
    }

    private ContaAcesso bloquearConta(Long id) {
        return contaRepository.buscarParaAtualizacao(id)
                .orElseThrow(CredenciaisInvalidasException::new);
    }

    private String hash(String valor) {
        if (valor == null || valor.isBlank() || valor.length() > 200) {
            throw new CredenciaisInvalidasException();
        }

        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(valor.getBytes(
                                    StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 indisponível", exception);
        }
    }
}