package com.github.rhaera.project.pocketbank.model.utility;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static com.github.rhaera.project.pocketbank.model.utility.UtilFormatacoes.ValidadorParaEmail;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidadorParaEmail.class)
@Documented
public @interface ValidarEmail {
    String message() default "Email inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
