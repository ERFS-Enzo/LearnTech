package com.curso.learntech.api.validation;

import com.curso.learntech.domain.DocumentoFiscal;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CpfValido, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null é aceito aqui: a obrigatoriedade pertence a @NotBlank.
        return value == null || DocumentoFiscal.cpfValido(value);
    }
}