package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.ApiError;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler{

    // 404 -> Trata erros de recurso não encontrado (ex: ID inexistente)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(error);
    }

    // 409 -> Trata conflitos de duplicação (ex: código de barras ou CPF já cadastrado)
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ApiError> handleRecursoDuplicado(
            RecursoDuplicadoException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(error);
    }

    // 400 -> Trata erros de validação dos DTOs (@Valid / @NotBlank / etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> fields = new HashMap<>();

        // Mapeia cada campo que falhou no teste para o seu erro correspondente
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.put(error.getField(), error.getDefaultMessage());
        }

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                "Bad Request",
                "Um ou mais campos são inválidos",
                request.getRequestURI(),
                fields
        );
        return ResponseEntity.status(status).body(error);
    }

    // 400 -> Trata JSON malformado (ex: vírgula ou chave faltando)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonInvalido(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                "Bad Request",
                "Mensagem JSON malformada ou sintaxe inválida",
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(status).body(error);
    }
}
