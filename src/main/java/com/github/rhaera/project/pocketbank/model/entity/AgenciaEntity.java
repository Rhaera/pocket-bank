package com.github.rhaera.project.pocketbank.model.entity;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;

import jakarta.persistence.*;

import lombok.Getter;

@Getter
@Entity
@Table(name = "agencies")
public class AgenciaEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id
    @Column(updatable = false) private Long id;
    @Column(name = "number")
    private String number;
    @Column(name = "address")
    private String address;
    @Column(name = "accounts")
    private Integer accounts;

    public AgenciaEntity() {
    }
    public AgenciaEntity(Agencia agencia) {
        number   = agencia.getCodigoAgencia();
        accounts = agencia.getContasAtivas().size();
    }
    public AgenciaEntity(Long id, String number, String address, Integer accounts) {
        this.id       = id;
        this.number   = number;
        this.address  = address;
        this.accounts = accounts;
    }

    public AgenciaDTO toDto() {
        return new AgenciaDTO(id, number, address, accounts).buildLink();
    }
}
