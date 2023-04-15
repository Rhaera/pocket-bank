package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

public class ContaCorrente extends ContaBancaria {

    public ContaCorrente(@NonNull String agencia, @NonNull String numeroConta, @NonNull Client client, @NonNull LocalDate criacaoDaConta, List<MovimentacaoFinanceira> extrato) {
        super(agencia, numeroConta, client, criacaoDaConta, extrato);
    }
}
