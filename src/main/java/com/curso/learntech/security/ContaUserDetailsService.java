package com.curso.learntech.security;

import com.curso.learntech.domain.ContaAcesso;
import com.curso.learntech.repository.ContaAcessoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ContaUserDetailsService implements UserDetailsService {

    private final ContaAcessoRepository repository;

    public ContaUserDetailsService(ContaAcessoRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        ContaAcesso conta = repository.findByLoginIgnoreCase(
                        ContaAcesso.normalizarLogin(username))
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Credenciais inválidas"));

        String[] authorities = conta.getPerfis().stream()
                .map(perfil -> "ROLE_" + perfil.name())
                .toArray(String[]::new);

        return User.withUsername(conta.getLogin())
                .password(conta.getSenhaHash())
                .authorities(authorities)
                .disabled(!conta.podeAutenticar())
                .build();
    }
}