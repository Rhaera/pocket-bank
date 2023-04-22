package com.github.rhaera.project.pocketbank.service.domain;

import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.service.abstraction.Emprestimo;

public final class EmprestimoPessoal extends Emprestimo {
    @Override
    public boolean definirSePossuiOfertaEmprestimo(ContaBancaria conta) {
        return false;
    }
}
