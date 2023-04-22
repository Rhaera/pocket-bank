package com.github.rhaera.project.pocketbank.model.dto.mongodb;

import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;
import com.github.rhaera.project.pocketbank.repository.ContaRepository;
import com.github.rhaera.project.pocketbank.model.entity.domain.Client;

import static com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria.TipoConta;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.springframework.hateoas.server.core.Relation;

import java.io.IOException;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(title = "SWAGGER TEST MongoDB")
@Relation(collectionRelation = "bank_agencies")
public final class RequisicaoCadastrarConta implements Serializable {

    private final Client client;
    private String numeroConta;
    private final Set<TipoConta> tipos = EnumSet.noneOf(TipoConta.class);
    private final ContaRepository repository;

    public RequisicaoCadastrarConta requisicaoValidada() {
        while (repository.findAll()
                        .stream()
                        .anyMatch(agencia -> {
                            try {
                                return agencia.getCodigoAgencia()
                                        .equals(UtilLocalizacao.agenciaMaisProxima(client.getCep())) &&
                                        agencia.getContasAtivas()
                                                .stream()
                                                .map(ContaBancaria::getNumeroConta)
                                                .collect(Collectors.toList())
                                                .contains(numeroConta);
                            } catch (IOException e) {
                                throw new IllegalStateException(e);
                            }
            })) criacaoNumeroConta();
        return this;
    }

    private void criacaoNumeroConta() {
        numeroConta = "";
        for (int i = 0; i <= 5; i++)
            numeroConta = numeroConta.concat(String.valueOf(new Random().nextInt(10)));
        numeroConta = numeroConta.substring(0, 5).concat(("-").concat(numeroConta.substring(5)));
    }

}
