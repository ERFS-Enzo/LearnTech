package com.curso.learntech.application;

import org.springframework.security.access.prepost.PreAuthorize;
import com.curso.learntech.api.dto.SegurancaDtos.ContaResponse;
import com.curso.learntech.api.dto.SegurancaDtos.CriarConta;
import com.curso.learntech.domain.ContaAcesso;
import com.curso.learntech.domain.PerfilAcesso;
import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.ContaAcessoRepository;
import com.curso.learntech.repository.PessoaRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Set;

@Service
public class ContaAcessoService {

    private final ContaAcessoRepository repository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoria;

    public ContaAcessoService(
            ContaAcessoRepository repository,
            PessoaRepository pessoaRepository,
            PasswordEncoder passwordEncoder,
            AuditoriaService auditoria) {

        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoria = auditoria;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ContaResponse criar(CriarConta request) {
        Pessoa pessoa = pessoaRepository.findById(request.pessoaId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Pessoa não encontrada"));

        ContaAcesso conta = cadastrar(
                pessoa,
                request.login(),
                request.senha(),
                request.perfis());

        auditoria.registrarAdministrativo(
                "CONTA_CRIADA",
                conta.getId());

        return resposta(conta);
    }

    @Transactional(readOnly = true)
    public ContaResponse minhaConta(String login) {
        return resposta(buscarPorLogin(login));
    }

    @Transactional
    public void alterarSenha(
            String login,
            String senhaAtual,
            String novaSenha) {

        ContaAcesso conta = buscarPorLogin(login);

        if (!passwordEncoder.matches(
                senhaAtual, conta.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        auditoria.registrarAdministrativo(
                "SENHA_ALTERADA",
                conta.getId());

        validarSenha(novaSenha);
        conta.alterarSenhaHash(passwordEncoder.encode(novaSenha));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ContaResponse alterarPerfis(
            Long id,
            Set<PerfilAcesso> perfis) {

        ContaAcesso conta = buscarParaAtualizacao(id);
        conta.alterarPerfis(perfis);

        auditoria.registrarAdministrativo(
                "PERFIS_ALTERADOS",
                conta.getId());

        return resposta(conta);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ContaResponse alterarStatus(Long id, boolean ativa) {
        ContaAcesso conta = buscarParaAtualizacao(id);
        conta.alterarStatus(ativa);

        auditoria.registrarAdministrativo(
                ativa ? "CONTA_ATIVADA" : "CONTA_DESATIVADA",
                conta.getId());

        return resposta(conta);
    }

    @Transactional
    public void inicializarAdministrador(
            String nome,
            String email,
            String cpf,
            String login,
            String senha) {

        if (repository.count() > 0) {
            return;
        }

        Pessoa pessoa = pessoaRepository.save(
                new Pessoa(nome, email, cpf, LocalDate.now()));

        cadastrar(
                pessoa,
                login,
                senha,
                Set.of(PerfilAcesso.ADMINISTRADOR));
    }

    private ContaAcesso cadastrar(
            Pessoa pessoa,
            String login,
            String senha,
            Set<PerfilAcesso> perfis) {

        if (pessoa.getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException(
                    "Pessoa deve estar ativa");
        }

        String normalizado = ContaAcesso.normalizarLogin(login);

        if (repository.existsByPessoaId(pessoa.getId())) {
            throw new RecursoDuplicadoException(
                    "Pessoa já possui conta");
        }

        if (repository.existsByLoginIgnoreCase(normalizado)) {
            throw new RecursoDuplicadoException(
                    "Login já cadastrado");
        }

        validarSenha(senha);

        return repository.save(new ContaAcesso(
                pessoa,
                normalizado,
                passwordEncoder.encode(senha),
                perfis));
    }

    private ContaAcesso buscarPorLogin(String login) {
        return repository.findByLoginIgnoreCase(
                        ContaAcesso.normalizarLogin(login))
                .orElseThrow(CredenciaisInvalidasException::new);
    }

    private ContaAcesso buscarParaAtualizacao(Long id) {
        return repository.buscarParaAtualizacao(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Conta não encontrada"));
    }

    private void validarSenha(String senha) {
        if (senha == null
                || senha.isBlank()
                || senha.length() < 8
                || senha.getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new IllegalArgumentException(
                    "Senha deve possuir pelo menos 8 caracteres "
                            + "e no máximo 72 bytes UTF-8");
        }
    }

    private ContaResponse resposta(ContaAcesso conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getPessoa().getId(),
                conta.getLogin(),
                conta.isAtiva(),
                conta.getPerfis());
    }
}