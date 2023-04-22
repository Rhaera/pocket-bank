package com.github.rhaera.project.pocketbank.service.abstraction;

import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface AnaliseDeCredito {
    BigInteger escoreMinimoParaEmprestimo = BigInteger.valueOf(50);

    BigDecimal definirLimiteEmprestimo(ContaBancaria conta);
    boolean definirSePossuiOfertaEmprestimo(ContaBancaria conta);

}
