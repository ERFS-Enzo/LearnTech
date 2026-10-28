package com.curso.learntech.repository;

public class RecursoEmUsoException extends RuntimeException {
    public RecursoEmUsoException(String message) {
        super(message);
    }
}