package com.curso.learntech.domain;

public final class DocumentoFiscal {

    private DocumentoFiscal() {
    }

    public static boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || digitosRepetidos(cpf)) {
            return false;
        }
        int primeiro = calcularDigito(cpf.substring(0, 9), 10);
        int segundo = calcularDigito(cpf.substring(0, 9) + primeiro, 11);
        return cpf.equals(cpf.substring(0, 9) + primeiro + segundo);
    }

    private static int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int indice = 0; indice < base.length(); indice++) {
            soma += Character.getNumericValue(base.charAt(indice))
                    * (pesoInicial - indice);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean digitosRepetidos(String valor) {
        return valor.chars().allMatch(digito -> digito == valor.charAt(0));
    }
}