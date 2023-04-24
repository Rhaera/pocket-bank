package com.github.rhaera.project.pocketbank.model.dto.mongodb;

import com.github.rhaera.project.pocketbank.controller.web.api.ContaController;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;
import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.repository.ContaRepository;

import static com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria.TipoConta;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(title = "SWAGGER TEST MongoDB")
@Relation(collectionRelation = "bank_agencies")
public final class RequisicaoCadastrarConta extends RepresentationModel<RequisicaoCadastrarConta> implements Serializable {
    private final static RequisicaoCadastrarConta INSTANCE = new RequisicaoCadastrarConta();

    @NonNull
    private Client client;

    @NonNull
    private String numeroConta;

    @NonNull
    private String numeroAgencia;

    private final Set<TipoConta> tipos = EnumSet.noneOf(TipoConta.class);

    private ContaRepository repository;

    private RequisicaoCadastrarConta(@NonNull Client client, @NonNull String number, @NonNull String agencyNumber) {
        this.client        = client;
        this.numeroConta   = number;
        this.numeroAgencia = agencyNumber;
    }

    public void requisicaoValidada() {
        while (validacaoNumeroContaECep())
            criacaoNumeroConta();
    }

    public boolean validacaoNumeroContaECep() {
        return repository.findAll()
                .stream()
                .anyMatch(agencia -> {
                    try {
                        return agencia.getCodigoAgencia()
                                .equals(UtilLocalizacao.agenciaMaisProxima(client.getCep())) &&
                                agencia.getContasAtivas()
                                        .stream()
                                        .map(ContaObject::getNumConta)
                                        .collect(Collectors.toList())
                                        .contains(numeroConta);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    public RequisicaoCadastrarConta buildLink() {
        return this.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContaController.class)
                        .getAccountByAgency(numeroAgencia, numeroConta))
                .withSelfRel());
    }

    private void criacaoNumeroConta() {
        numeroConta = "";
        for (int i = 0; i <= 5; i++)
            numeroConta = numeroConta.concat(String.valueOf(new Random().nextInt(10)));
        numeroConta = numeroConta.substring(0, 5).concat(("-").concat(numeroConta.substring(5)));
    }

    public static RequisicaoCadastrarConta getInstance(Client client, String number, String agencyNumber) {
        INSTANCE.client        = client;
        INSTANCE.numeroConta   = number;
        INSTANCE.numeroAgencia = agencyNumber;
        return INSTANCE;
    }

    public static RequisicaoCadastrarConta getInstance() {
        INSTANCE.criacaoNumeroConta();
        return INSTANCE;
    }
}
