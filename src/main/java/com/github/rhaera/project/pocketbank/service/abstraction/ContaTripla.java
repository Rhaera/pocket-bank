package com.github.rhaera.project.pocketbank.service.abstraction;

import org.springframework.stereotype.Service;

@Service
public interface ContaTripla extends ContaMultiModal, ContaCorrenteEPoupanca, ContaCorrenteESalario {

}
