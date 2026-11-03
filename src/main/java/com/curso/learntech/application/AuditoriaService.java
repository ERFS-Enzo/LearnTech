package com.curso.learntech.application;

import com.curso.learntech.domain.EventoSeguranca;
import com.curso.learntech.repository.ContaAcessoRepository;
import com.curso.learntech.repository.EventoSegurancaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class AuditoriaService {

    private final EventoSegurancaRepository repository;
    private final ContaAcessoRepository contaRepository;
    private final Clock clock;

    public AuditoriaService(
            EventoSegurancaRepository repository,
            ContaAcessoRepository contaRepository,
            Clock clock) {

        this.repository = repository;
        this.contaRepository = contaRepository;
        this.clock = clock;
    }

    @Transactional
    public void registrar(
            String tipo,
            Long atorId,
            Long alvoId,
            String chaveLogin,
            String origem) {

        repository.save(new EventoSeguranca(
                clock.instant(),
                tipo,
                atorId,
                alvoId,
                chaveLogin,
                origem));
    }

    @Transactional
    public void registrarAdministrativo(
            String tipo,
            Long alvoId) {

        var authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long atorId = authentication == null
                ? null
                : contaRepository.findByLoginIgnoreCase(
                        authentication.getName())
                .map(conta -> conta.getId())
                .orElse(null);

        registrar(tipo, atorId, alvoId, null, null);
    }
}