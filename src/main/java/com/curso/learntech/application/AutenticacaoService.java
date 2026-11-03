package com.curso.learntech.application;

import com.curso.learntech.api.dto.AutenticacaoDtos.TokenResponse;
import com.curso.learntech.domain.ContaAcesso;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final SessaoAutenticacaoService sessaoService;
    private final ProtecaoLoginService protecaoService;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            SessaoAutenticacaoService sessaoService,
            ProtecaoLoginService protecaoService) {

        this.authenticationManager = authenticationManager;
        this.sessaoService = sessaoService;
        this.protecaoService = protecaoService;
    }

    @Transactional(noRollbackFor = CredenciaisInvalidasException.class)
    public TokenResponse autenticar(
            String login,
            String senha,
            String origem) {

        String normalizado = ContaAcesso.normalizarLogin(login);

        // O lock permanece durante a autenticação.
        // Tentativas simultâneas do mesmo login são serializadas.
        var protecao =
                protecaoService.bloquearRegistro(normalizado);

        if (!protecaoService.permitir(
                protecao, normalizado, origem)) {

            throw new CredenciaisInvalidasException();
        }

        try {
            var authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            normalizado,
                            senha));

            var tokens = sessaoService.iniciar(authentication);

            protecaoService.sucesso(
                    protecao, normalizado, origem);

            return tokens;

        } catch (AuthenticationException exception) {
            protecaoService.falha(
                    protecao, normalizado, origem);

            throw new CredenciaisInvalidasException();
        }
    }
}