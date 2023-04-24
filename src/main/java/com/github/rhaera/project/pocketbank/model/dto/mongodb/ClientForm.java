package com.github.rhaera.project.pocketbank.model.dto.mongodb;

import com.github.rhaera.project.pocketbank.controller.web.api.ClientController;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.*;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(title = "Client immutable data for MongoDB")
@Relation(collectionRelation = "pocket_bank_clients")
public class ClientForm extends RepresentationModel<ClientForm> implements Serializable {
    private String cpf;

    private LocalDate dataNascimento;

    private Long cepDeOrigem;

    public ClientForm toHyperMedia() {
        return this.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClientController.class)
                                                                .getById(cpf.trim()))
                                                                .withSelfRel());
    }
}
