package com.curso.learntech.api.controller;

import com.curso.learntech.api.dto.ApiError;
import com.curso.learntech.repository.RecursoDuplicadoException;
import com.curso.learntech.repository.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
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
public class ApiExceptionHandler {

    // 404 -> recurso não encontrado (ex: ID inexistente)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, null);
    }

    // 409 -> valor que deve ser único já cadastrado (ex: código do curso, CPF)
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ApiError> handleRecursoDuplicado(
            RecursoDuplicadoException ex, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request, null);
    }

    // 400 -> validação dos DTOs (@Valid, @NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidacao(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fields = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return resposta(HttpStatus.BAD_REQUEST, "Bad Request",
                "Um ou mais campos são inválidos", request, fields);
    }

    // 400 -> JSON malformado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJsonInvalido(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Bad Request",
                "Mensagem JSON malformada ou sintaxe inválida", request, null);
    }

    // 400 -> regra de domínio violada (ex: valor negativo)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleRegraDeDominio(
            IllegalArgumentException ex, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request, null);
    }

    // 409 -> estado inválido ou violação de unicidade no banco
    @ExceptionHandler({IllegalStateException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleConflitoDeDados(
            Exception ex, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, "Conflict",
                "Operação conflita com dados já existentes", request, null);
    }

    private ResponseEntity<ApiError> resposta(
            HttpStatus status,
            String tipo,
            String mensagem,
            HttpServletRequest request,
            Map<String, String> fields) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                tipo,
                mensagem,
                request.getRequestURI(),
                fields);
        return ResponseEntity.status(status).body(error);
    }
}