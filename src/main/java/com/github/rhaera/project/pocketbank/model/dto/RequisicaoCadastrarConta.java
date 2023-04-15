package com.github.rhaera.project.pocketbank.model.dto;

import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class RequisicaoCadastrarConta implements Serializable {

    private ContaBancaria contaBancaria;
    //custom constructor
}
