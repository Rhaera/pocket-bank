package com.github.rhaera.project.pocketbank.model.entity.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Client {

    private String nome;
    private String cep;
    private String celular;
    private final String cpf;
    private final LocalDate dataNascimento;

}
