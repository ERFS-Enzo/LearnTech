package com.curso.learntech.application;

import com.curso.learntech.api.dto.AutenticacaoDtos.TokenResponse;
import com.curso.learntech.domain.ContaAcesso;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            TokenService tokenService) {

        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenResponse autenticar(String login, String senha) {
        try {
            var authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            ContaAcesso.normalizarLogin(login),
                            senha));

            var token = tokenService.emitir(authentication, null);

            return new TokenResponse(
                    token.valor(),
                    "Bearer",
                    token.expiresIn());

        } catch (AuthenticationException exception) {
            throw new CredenciaisInvalidasException();
        }
    }
}