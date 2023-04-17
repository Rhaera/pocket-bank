package com.github.rhaera.project.pocketbank.model.dto.sql;

import com.github.rhaera.project.pocketbank.controller.AgenciaController;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

@Getter
@Setter
@AllArgsConstructor
@Schema(title = "SWAGGER TEST")
@Relation(collectionRelation = "agencies")
public class AgenciaDTO extends RepresentationModel<AgenciaDTO> {

    private Long id;
    private String number;
    private Integer accounts;
    public AgenciaDTO buildLink() {
        return this.add(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(AgenciaController.class).getAgency(id))
                .withSelfRel());
    }
    public AgenciaEntity toEntity() {
        return new AgenciaEntity(id, number, accounts);
    }
}
