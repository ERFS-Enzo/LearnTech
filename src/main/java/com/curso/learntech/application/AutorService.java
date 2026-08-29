package com.curso.learntech.application;

import com.curso.learntech.domain.Autor;
import com.curso.learntech.repository.AutorRepository;
import com.curso.learntech.repository.RecursoDuplicadoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<Autor> buscarTodos(){
        return autorRepository.findAll();
    }

    @Transactional
    public Autor cadastrar(Autor autor, Long AutorId){
        if(autorRepository.existsByCpf(
                autor.getCpf())){
            throw new RecursoDuplicadoException("Cpf ja cadastrado!");
        }

        return autorRepository.save(autor);
    }
}
