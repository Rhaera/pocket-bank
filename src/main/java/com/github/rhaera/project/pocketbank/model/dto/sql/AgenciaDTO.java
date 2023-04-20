package com.github.rhaera.project.pocketbank.model.dto.sql;

import com.github.rhaera.project.pocketbank.controller.AgenciaController;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(title = "SWAGGER TEST")
@Relation(collectionRelation = "agencies")
public class AgenciaDTO extends RepresentationModel<AgenciaDTO> implements Serializable {

    private Long id;
    private String number;
    private String address;
    private Integer accounts;

    public AgenciaDTO(Long id, String number, String address, Integer accounts) {
        this.id       = id;
        this.number   = number;
        this.address  = address;
        this.accounts = accounts;
    }

    public AgenciaDTO buildLink() {
        return this.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AgenciaController.class)
                        .getAgency(id))
                .withSelfRel());
    }

    public AgenciaEntity toEntity() {
        return new AgenciaEntity(id, number, address, accounts);
    }
}
