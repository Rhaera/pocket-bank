package com.github.rhaera.project.pocketbank.model.utility;

import com.github.rhaera.project.pocketbank.controller.exception.FormatacaoIlegalException;
import com.github.rhaera.project.pocketbank.controller.exception.IdadeIlegalException;
import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public final class UtilFormatacoes {
    private UtilFormatacoes() {

    }

    public static class ValidadorParaEmail implements ConstraintValidator<ValidarEmail, String> {

        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
        
        @Override
        public void initialize(ValidarEmail constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
        }

        @Override
        public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
            return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
        }
    }

    public static class ValidadorParaSenhas implements ConstraintValidator<ConfirmarSenha, Object> {

        @Override
        public void initialize(ConfirmarSenha constraintAnnotation) {
            ConstraintValidator.super.initialize(constraintAnnotation);
        }

        @Override
        public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
            ClientDTO dto = (ClientDTO) o;
            return dto.getSenha().equals(dto.getConfirmarSenha());
        }
    }

    public static <T> String formatarCPF(T cpf) {
        apenasNumerosEValidarTamanho(cpf, 11);
        String cpfFormat = String.valueOf(cpf).trim();
        return String.format("%s.%s.%s-%s", cpfFormat.substring(0, 3), cpfFormat.substring(3, 6), cpfFormat.substring(6, 9), cpfFormat.substring(9));
    }

    public static <T> String formatarCelular(T celular) {
        apenasNumerosEValidarTamanho(celular, 11);
        String celularFormat = String.valueOf(celular).trim();
        return String.format("(%s) %s %s-%s", celularFormat.substring(0, 2), celularFormat.charAt(2), celularFormat.substring(3, 7), celularFormat.substring(7));
    }

    public static <T> LocalDate formatarDataNascimento(T dataNascimento) {
        apenasNumerosEValidarTamanho(dataNascimento, 8);
        String dataNascimentoFormat = String.valueOf(dataNascimento).trim();
        dataNascimentoFormat = String.format("%s/%s/%s", dataNascimentoFormat.substring(0, 2),
                                            dataNascimentoFormat.substring(2, 4),
                                            dataNascimentoFormat.substring(4));
        if (LocalDate.of(
                        Integer.parseInt(dataNascimentoFormat.substring(6)),
                        Integer.parseInt(dataNascimentoFormat.substring(3, 5)),
                        Integer.parseInt(dataNascimentoFormat.substring(0, 2))
                        ).plusYears(18)
                        .isAfter(LocalDate.now()))
            throw new IdadeIlegalException("Menor de 18 anos!");
        return LocalDate.of(
                Integer.parseInt(dataNascimentoFormat.substring(6)),
                Integer.parseInt(dataNascimentoFormat.substring(3, 5)),
                Integer.parseInt(dataNascimentoFormat.substring(0, 2))
        );
    }

    public static <T> String validarFormatacaoEmail(T email) {
        String emailFormat = String.valueOf(email).trim();
        if (emailFormat.contains(" ") ||
            !emailFormat.contains("@") ||
            !(emailFormat.endsWith(".com") || emailFormat.endsWith(".com.br") || emailFormat.endsWith(".br") || emailFormat.endsWith(".me")))
            throw new FormatacaoIlegalException("Email inválido!");
        return emailFormat;
    }

    public static <T> String formatarEGerarChavePixAleatoria(T secret) {
        return String.valueOf(secret)
                .concat(Arrays.toString(UUID.randomUUID()
                        .toString()
                        .concat(Arrays.toString(Base64.getEncoder()
                                        .encode(UUID.randomUUID()
                                        .toString()
                                        .getBytes())))
                        .split("-")));
    }

    private static <T> void apenasNumerosEValidarTamanho(T conteudo, int tamanho) {
        if (String.valueOf(conteudo).trim().length() != tamanho)
            throw new FormatacaoIlegalException("Dados inválidos! O campo solicitado precisa de exatamente " + tamanho + " dígitos (sem letras) para ser verificado.");
        try {
            Long.parseLong(String.valueOf(conteudo).trim());
        } catch (NumberFormatException e) {
            throw new FormatacaoIlegalException("Por favor, insira apenas dígitos para validar.");
        }
    }
}
