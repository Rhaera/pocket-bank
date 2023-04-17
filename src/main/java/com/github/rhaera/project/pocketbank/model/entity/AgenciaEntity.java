package com.github.rhaera.project.pocketbank.model.entity;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "agencies")
public class AgenciaEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id @Column(updatable = false) private Long id;
    private String number;
    private Integer accounts;

    public AgenciaEntity(Agencia agencia) {
        number   = agencia.getCodigoAgencia();
        accounts = agencia.getContasAtivas().size();
    }
    public AgenciaEntity(Long id, String number, Integer accounts) {
        this.id       = id;
        this.number   = number;
        this.accounts = accounts;
    }

    public AgenciaDTO toDto() {
        return new AgenciaDTO(id, number, accounts).buildLink();
    }
}
