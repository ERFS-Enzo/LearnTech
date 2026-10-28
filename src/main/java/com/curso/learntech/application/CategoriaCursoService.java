package com.curso.learntech.application;

import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Set;

@Service
public class CategoriaCursoService {
    private final CategoriaCursoRepository repository;
    private final CursoRepository cursoRepository;

    public CategoriaCursoService(CategoriaCursoRepository repository,
                                 CursoRepository cursoRepository) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public CategoriaCurso cadastrar(String nome) {
        String nomeNormalizado = nome == null ? null : nome.trim();

        if (repository.existsByNomeIgnoreCase(nomeNormalizado)) {
            throw new RecursoDuplicadoException("Nome da categoria já cadastrada");
        }
        return repository.save(new CategoriaCurso(nomeNormalizado));
    }

    @Transactional(readOnly = true)
    public CategoriaCurso buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaCurso> pesquisar(String nome, Status status, Pageable pageable) {
        validarOrdenacao(pageable, Set.of("id", "nome", "status"));

        Specification<CategoriaCurso> filtros =
                (root, query, cb) -> cb.conjunction();

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
    public CategoriaCurso alterar(Long id, String nome) {
        String nomeNormalizado = nome == null ? null : nome.trim();

        if (repository.existsByNomeIgnoreCaseAndIdNot(nomeNormalizado, id)) {
            throw new RecursoDuplicadoException("Nome da categoria já cadastrada");
        }

        CategoriaCurso categoria = buscarEntidade(id);
        categoria.alterarNome(nomeNormalizado);
        return categoria;
    }

    @Transactional
    public CategoriaCurso alterarStatus(Long id, Status status) {
        CategoriaCurso categoria = buscarEntidade(id);
        if (status == Status.ATIVO) {
            categoria.ativar();
        } else {
            categoria.inativar();
        }
        return categoria;
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaCurso categoria = buscarEntidade(id);

        if (cursoRepository.existsByCategoriaId(id)) {
            throw new RecursoEmUsoException(
                    "Categoria não pode ser excluída porque possui cursos");
        }

        repository.delete(categoria);
        repository.flush();
    }

    private CategoriaCurso buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de curso não encontrado"));
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