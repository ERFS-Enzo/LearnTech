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
    private final SessaoAutenticacaoService sessaoService;

    public AutenticacaoService(
            AuthenticationManager authenticationManager,
            SessaoAutenticacaoService sessaoService) {

        this.authenticationManager = authenticationManager;
        this.sessaoService = sessaoService;
    }

    public TokenResponse autenticar(String login, String senha) {
        try {
            var authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            ContaAcesso.normalizarLogin(login),
                            senha));

            return sessaoService.iniciar(authentication);

        } catch (AuthenticationException exception) {
            throw new CredenciaisInvalidasException();
        }
    }
}