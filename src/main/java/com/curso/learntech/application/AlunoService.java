package com.curso.learntech.application;

import com.curso.learntech.domain.Aluno;
import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.AlunoRepository;
import com.curso.learntech.repository.PessoaRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AlunoService {

    private final AlunoRepository repository;
    private final PessoaRepository pessoaRepository;

    public AlunoService(AlunoRepository repository, PessoaRepository pessoaRepository) {
        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public Aluno cadastrar(Long pessoaId, String telefone) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));

        if (pessoa.getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Pessoa deve estar ativa");
        }
        if (repository.existsByPessoaId(pessoaId)) {
            throw new RecursoDuplicadoException("Pessoa já possui cadastro de aluno");
        }

        return repository.save(new Aluno(pessoa, telefone));
    }

    @Transactional(readOnly = true)
    public Aluno buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public Page<Aluno> listar(Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "telefone"));
        return repository.findAll(pageable);
    }

    @Transactional
    public Aluno alterarTelefone(Long id, String telefone) {
        Aluno aluno = buscarEntidade(id);
        aluno.alterarTelefone(telefone);
        return aluno;
    }

    private Aluno buscarEntidade(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado"));
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