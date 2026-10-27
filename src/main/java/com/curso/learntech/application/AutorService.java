package com.curso.learntech.application;

import com.curso.learntech.domain.Autor;
import com.curso.learntech.repository.AutorRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor cadastrar(Autor autor) {
        if (autorRepository.existsByCpf(autor.getCpf())) {
            throw new RecursoDuplicadoException("Cpf ja cadastrado!");
        }
        return autorRepository.save(autor);
    }

    @Transactional(readOnly = true)
    public Autor buscarPorId(Long id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Autor não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Autor> listar() {
        return autorRepository.findAll();
    }
}