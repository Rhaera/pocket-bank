package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.github.rhaera.project.pocketbank.model.dto.sql.ClientDTO;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;

import jakarta.persistence.*;

import lombok.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_clients")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false) private Long id;
    @Column(nullable = false, name = "cpf", unique = true) private String cpf;
    @Column(nullable = false, name = "name") private String name;
    @Column(nullable = false, name = "username") private String username;
    @Column(nullable = false, name = "email") private String email;
    @Column(nullable = false, name = "password") private String senha;
    @Column(nullable = false, name = "postal") private String cep;
    @Column(nullable = false, name = "birth") private String dataNascimento;
    @Column(nullable = false, name = "role") private String role;
    @Column(name = "cellphone") private String celular;
    @Column(name = "gender") private String genero;
    @Column(name = "job") private String emprego;
    @Column(name = "revenue") private Long rendaMensal;

    public enum Gender {
        MALE(1),
        FEMALE(2),
        NOT_INFORMED(3);

        private final int gender;

        Gender(int gender) {
            this.gender = gender;
        }

        @Override
        public String toString() {
            return gender == 1 ? "M" : (gender == 2 ? "F" : "N/A");
        }
    }

    public void atualizarDadosViaveis(ClientEntity dadosAtualizados) throws IOException {
        this.name        = dadosAtualizados.name;
        this.email       = dadosAtualizados.email; // verify
        this.senha       = dadosAtualizados.senha; // format
        if (!Objects.equals(UtilLocalizacao.agenciaMaisProxima(dadosAtualizados.cep), "0000"))
            this.cep     = dadosAtualizados.cep;
        this.celular     = dadosAtualizados.celular; // verify
        this.emprego     = dadosAtualizados.emprego;
        this.rendaMensal = dadosAtualizados.rendaMensal;
    }
    public String getNonNullCpf() {
        if (this.cpf == null) this.cpf = "00000000000";
        return this.cpf;
    }
    public ClientDTO toDto() {
        ClientDTO dto = new ClientDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setCep(cep);
        dto.setDataNascimento(dataNascimento);
        dto.setCpf(cpf);
        dto.setCelular(celular);
        return dto;
    }
    public void usernameFormatter() {
        if (name.trim().contains(" ")) {
            username = name.substring(0, name.trim().indexOf(" ")).concat(cpf.concat("@pocket-bank"));
            return;
        }
        username = name.trim().concat(cpf.concat("@pocket-bank"));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientEntity client = (ClientEntity) o;
        return (cpf != null && name != null) &&
                cpf.equals(client.cpf) &&
                username.equals(client.username);
    }
    @Override
    public int hashCode() {
        return (cpf.hashCode() << 5) * (cpf.hashCode() >> 5) - cpf.hashCode();
    }
}
