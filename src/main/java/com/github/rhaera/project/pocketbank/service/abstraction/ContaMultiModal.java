package com.github.rhaera.project.pocketbank.service.abstraction;

import static com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria.TipoConta;

import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public interface ContaMultiModal {

    boolean definirModoDeUsor(EnumSet<? extends TipoConta> opicoesDeConta);

}
