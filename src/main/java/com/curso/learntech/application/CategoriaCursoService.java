package com.curso.learntech.application;

import com.curso.learntech.domain.CategoriaCurso;
import com.curso.learntech.repository.CategoriaCursoRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaCursoService {
    private final CategoriaCursoRepository repository;

    public CategoriaCursoService(CategoriaCursoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaCurso cadastrar(String nome) {
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new RecursoDuplicadoException("Nome da categoria já cadastrada");
        }
        return repository.save(new CategoriaCurso(nome));
    }

    @Transactional(readOnly = true)
    public CategoriaCurso buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria de curso não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<CategoriaCurso> listar() {
        return repository.findAll();
    }
}