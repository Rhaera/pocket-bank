package com.github.rhaera.project.pocketbank.model.entity.interfaces;

import org.springframework.stereotype.Service;

@Service
public interface ContaCorrenteESalario extends ContaMultiModal {

    void definirPortabilidade();
}
