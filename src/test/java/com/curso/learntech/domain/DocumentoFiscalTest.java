package com.curso.learntech.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentoFiscalTest {

    @Test
    void deveAceitarCpfValido() {
        assertTrue(DocumentoFiscal.cpfValido("52998224725"));
        assertTrue(DocumentoFiscal.cpfValido("11144477735"));
    }

    @Test
    void deveRejeitarCpfComDigitoVerificadorErrado() {
        assertFalse(DocumentoFiscal.cpfValido("52998224724"));
    }

    @Test
    void deveRejeitarCpfComDigitosRepetidos() {
        assertFalse(DocumentoFiscal.cpfValido("11111111111"));
        assertFalse(DocumentoFiscal.cpfValido("00000000000"));
    }

    @Test
    void deveRejeitarCpfNuloOuComFormatoInvalido() {
        assertFalse(DocumentoFiscal.cpfValido(null));
        assertFalse(DocumentoFiscal.cpfValido("529.982.247-25"));
        assertFalse(DocumentoFiscal.cpfValido("123"));
    }
}