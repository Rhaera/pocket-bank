package com.github.rhaera.project.pocketbank.service.abstraction;

import static com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria.TipoConta;

import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public interface ContaMultiModal {

    boolean cartaoDebito = false;

    boolean definirModoDeUsor(EnumSet<? extends TipoConta> opicoesDeConta);

    default boolean possuiCartaoDebito(ContaBancaria conta) {
        return true;
    }

}
