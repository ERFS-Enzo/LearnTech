package com.curso.learntech.application;

import com.curso.learntech.domain.Colaborador;
import com.curso.learntech.domain.FuncaoColaborador;
import com.curso.learntech.domain.Pessoa;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.ColaboradorRepository;
import com.curso.learntech.repository.PessoaRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
public class ColaboradorService {

    private final ColaboradorRepository repository;
    private final PessoaRepository pessoaRepository;

    public ColaboradorService(ColaboradorRepository repository,
                              PessoaRepository pessoaRepository) {
        this.repository = repository;
        this.pessoaRepository = pessoaRepository;
    }

    @Transactional
    public Colaborador cadastrar(Long pessoaId,
                                 String matricula,
                                 FuncaoColaborador funcao,
                                 LocalDate dataAdmissao) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa não encontrada"));

        if (pessoa.getStatus() != Status.ATIVO) {
            throw new IllegalArgumentException("Pessoa deve estar ativa");
        }
        if (repository.existsByPessoaId(pessoaId)) {
            throw new RecursoDuplicadoException("Pessoa já possui vínculo de colaborador");
        }

        String matriculaNormalizada = matricula == null ? null : matricula.trim();
        if (matriculaNormalizada != null && repository.existsByMatricula(matriculaNormalizada)) {
            throw new RecursoDuplicadoException("Matrícula já cadastrada");
        }

        return repository.save(new Colaborador(pessoa, matricula, funcao, dataAdmissao));
    }

    @Transactional(readOnly = true)
    public Colaborador buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public Page<Colaborador> listar(Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "matricula", "funcao", "dataAdmissao"));
        return repository.findAll(pageable);
    }

    @Transactional
    public Colaborador alterar(Long id, String matricula, FuncaoColaborador funcao) {
        String matriculaNormalizada = matricula == null ? null : matricula.trim();
        if (matriculaNormalizada != null
                && repository.existsByMatriculaAndIdNot(matriculaNormalizada, id)) {
            throw new RecursoDuplicadoException("Matrícula já cadastrada");
        }

        Colaborador colaborador = buscarEntidade(id);
        colaborador.alterar(matricula, funcao);
        return colaborador;
    }

    private Colaborador buscarEntidade(Long id) {
        return repository.findOneById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Colaborador não encontrado"));
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