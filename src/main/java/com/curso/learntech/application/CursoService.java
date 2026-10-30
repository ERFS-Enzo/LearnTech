package com.curso.learntech.application;

import com.curso.learntech.domain.Autor;
import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.domain.Curso;
import com.curso.learntech.domain.Status;
import com.curso.learntech.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final CategoriaCursoRepository categoriaRepository;
    private final AutorRepository autorRepository;

    public CursoService(CursoRepository cursoRepository,
                        CategoriaCursoRepository categoriaRepository,
                        AutorRepository autorRepository) {
        this.cursoRepository = cursoRepository;
        this.categoriaRepository = categoriaRepository;
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Curso cadastrar(Curso curso, Long categoriaId, Long autorId) {
        if (cursoRepository.existsByCodigoCurso(curso.getCodigoCurso())) {
            throw new RecursoDuplicadoException("Código do curso já cadastrado");
        }

        CategoriaCurso categoria = buscarCategoria(categoriaId);

        if (autorId != null) {
            curso.atribuirAutor(buscarAutor(autorId));
        }

        categoria.adicionarCurso(curso);
        return cursoRepository.save(curso);
    }

    @Transactional(readOnly = true)
    public Curso buscarPorId(Long id) {
        return buscarEntidade(id);
    }

    @Transactional(readOnly = true)
    public Page<Curso> pesquisar(String descricao,
                                 Status status,
                                 Long categoriaId,
                                 Long autorId,
                                 Pageable pageable) {
        validarOrdenacao(pageable, Set.of(
                "id", "codigoCurso", "descricao", "cargaHoraria",
                "valorCurso", "quantidadeModulos", "dataCadastro", "status"));

        Specification<Curso> filtros = (root, query, cb) -> cb.conjunction();

        if (descricao != null && !descricao.isBlank()) {
            String trecho = "%" + descricao.trim().toLowerCase(Locale.ROOT) + "%";
            filtros = filtros.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("descricao")), trecho));
        }
        if (status != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }
        if (categoriaId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("categoria").get("id"), categoriaId));
        }
        if (autorId != null) {
            filtros = filtros.and((root, query, cb) ->
                    cb.equal(root.get("autor").get("id"), autorId));
        }

        return cursoRepository.findAll(filtros, pageable);
    }

    @Transactional
    public Curso alterar(Long id,
                         String descricao,
                         BigDecimal valorCurso,
                         int quantidadeModulos,
                         Long categoriaId,
                         Long autorId) {
        Curso curso = buscarEntidade(id);
        CategoriaCurso categoria = buscarCategoria(categoriaId);
        Autor autor = autorId == null ? null : buscarAutor(autorId);

        curso.alterarDescricao(descricao);
        curso.alterarValorCurso(valorCurso);
        curso.alterarQuantidadeModulos(quantidadeModulos);
        curso.alterarCategoria(categoria);
        curso.alterarAutor(autor);
        return curso;
    }

    @Transactional
    public Curso alterarStatus(Long id, Status status) {
        Curso curso = buscarEntidade(id);
        if (status == Status.ATIVO) {
            curso.ativar();
        } else {
            curso.inativar();
        }
        return curso;
    }

    @Transactional
    public Curso acrescentarCargaHoraria(Long id, BigDecimal quantidade) {
        Curso curso = buscarEntidade(id);
        curso.acrescentarCargaHoraria(quantidade);
        return curso;
    }

    @Transactional
    public Curso reduzirCargaHoraria(Long id, BigDecimal quantidade) {
        Curso curso = buscarEntidade(id);
        curso.reduzirCargaHoraria(quantidade);
        return curso;
    }

    @Transactional
    public Curso definirVagas(Long id, int vagas) {
        Curso curso = buscarEntidade(id);
        curso.definirVagas(vagas);
        return curso;
    }

    @Transactional
    public void excluir(Long id) {
        Curso curso = buscarEntidade(id);
        cursoRepository.delete(curso);
        cursoRepository.flush();
    }

    private Curso buscarEntidade(Long id) {
        return cursoRepository.findByIdComCategoria(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Curso não encontrado"));
    }

    private CategoriaCurso buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de curso não encontrado"));
    }

    private Autor buscarAutor(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Autor não encontrado"));
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