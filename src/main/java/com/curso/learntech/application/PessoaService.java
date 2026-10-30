package com.curso.learntech.application;

import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.PessoaRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class PessoaService {

    private final PessoaRepository repository;

    public PessoaService(PessoaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pessoa cadastrar(Pessoa pessoa) {
        if (repository.existsByCpf(pessoa.getCpf())) {
            throw new RecursoDuplicadoException("CPF já cadastrado");
        }
        if (repository.existsByEmailIgnoreCase(pessoa.getEmail())) {
            throw new RecursoDuplicadoException("E-mail já cadastrado");
        }
        return repository.save(pessoa);
    }

    @Transactional(readOnly = true)
    public Pessoa buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public Page<Pessoa> pesquisar(String nome, Status status, Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "nome", "email", "dataCadastro", "status"));

        Specification<Pessoa> filtros = (root, query, cb) -> cb.conjunction();

        if (nome != null && !nome.isBlank()) {
            String trecho = "%" + nome.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("nome")), trecho));
        }
        if (status != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        return repository.findAll(filtros, pageable);
    }

    @Transactional
    public Pessoa alterar(Long id, String nome, String email) {
        Pessoa pessoa = buscarEntidade(id);
        String emailNormalizado = Pessoa.normalizarEmail(email);

        if (emailNormalizado != null
                && repository.existsByEmailIgnoreCaseAndIdNot(emailNormalizado, id)) {
            throw new RecursoDuplicadoException("E-mail já cadastrado");
        }

        pessoa.alterarDados(nome, email);
        return pessoa;
    }

    @Transactional
    public Pessoa alterarStatus(Long id, Status status) {
        Pessoa pessoa = buscarEntidade(id);
        if (status == Status.ATIVO) {
            pessoa.ativar();
        } else {
            pessoa.inativar();
        }
        return pessoa;
    }

    private Pessoa buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));
    }

    private void validarOrdenacao(Pageable pageable, Set<String> camposPermitidos) {
        pageable.getSort().forEach(ordem -> {
            if (!camposPermitidos.contains(ordem.getProperty())) {
                throw new IllegalArgumentException(
                        "Campo de ordenação inválido: " + ordem.getProperty());
            }
        });
    }
}