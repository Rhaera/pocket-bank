package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.github.rhaera.project.pocketbank.model.entity.abstraction.ContaBancaria;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("bank_agencies")
public class Agencia {
    @Id
    private String codigoAgencia;

    @DBRef @Field("agency_accounts") private List<ContaBancaria> contasAtivas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agencia other = (Agencia) o;
        return this.codigoAgencia.equals(other.codigoAgencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoAgencia.concat(codigoAgencia)); // (i << 5) - i
    }
}
