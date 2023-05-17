package com.github.rhaera.project.pocketbank.model.utility;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static com.github.rhaera.project.pocketbank.model.utility.UtilFormatacoes.ValidadorParaSenhas;

@Target({TYPE,ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidadorParaSenhas.class)
@Documented
public @interface ConfirmarSenha {
    String message() default "Senhas diferem entre si";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
