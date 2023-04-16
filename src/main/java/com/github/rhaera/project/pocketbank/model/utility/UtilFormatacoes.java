package com.github.rhaera.project.pocketbank.model.utility;

import com.github.rhaera.project.pocketbank.controller.exception.FormatacaoIlegalException;
import com.github.rhaera.project.pocketbank.controller.exception.IdadeIlegalException;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public final class UtilFormatacoes {

    private UtilFormatacoes() {
    }

    public static <T> String formatarCPF(T cpf) {
        if (String.valueOf(cpf).trim().length() != 11) throw new FormatacaoIlegalException("CPF inválido!");
        String cpfFormat = String.valueOf(cpf).trim();
        return String.format("%s.%s.%s-%s", cpfFormat.substring(0, 3), cpfFormat.substring(3, 6), cpfFormat.substring(6, 9), cpfFormat.substring(9));
    }

    public static <T> String formatarCelular(T celular) {
        if (String.valueOf(celular).trim().length() != 11) throw new FormatacaoIlegalException("Telefone inválido!");
        String celularFormat = String.valueOf(celular).trim();
        return String.format("(%s) %s %s-%s", celularFormat.substring(0, 2), celularFormat.charAt(2), celularFormat.substring(3, 7), celularFormat.substring(7));
    }

    public static <T> String formatarDataNascimento(T dataNascimento) {
        if (String.valueOf(dataNascimento).trim().length() != 11) throw new FormatacaoIlegalException("Data de nascimento inválida!");
        String dataNascimentoFormat = String.valueOf(dataNascimento).trim();
        dataNascimentoFormat = String.format("%s/%s/%s", dataNascimentoFormat.substring(0, 2),
                                            dataNascimentoFormat.substring(2, 4),
                                            dataNascimentoFormat.substring(4));
        if (LocalDate.of(Integer.parseInt(dataNascimentoFormat.substring(0, 2)),
                            Integer.parseInt(dataNascimentoFormat.substring(3, 5)),
                            Integer.parseInt(dataNascimentoFormat.substring(6))).plusYears(18).isAfter(LocalDate.now())) throw new IdadeIlegalException("Menor de 18 anos!");
        return dataNascimentoFormat;
    }
}
