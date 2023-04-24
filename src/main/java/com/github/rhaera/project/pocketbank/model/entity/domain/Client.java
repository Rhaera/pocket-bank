package com.github.rhaera.project.pocketbank.model.entity.domain;

import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Document(collection = "clients")
public class Client {
    private String nome;
    private String email;
    private String senha;
    private String cep;
    private String celular;
    private String cpf;
    private final LocalDate dataNascimento;
    public void atualizarDadosViaveis(Client dadosAtualizados) throws IOException {
        this.nome    = dadosAtualizados.nome;
        this.email   = dadosAtualizados.email; // verify
        this.senha   = dadosAtualizados.senha; // format
        if (!Objects.equals(UtilLocalizacao.agenciaMaisProxima(dadosAtualizados.cep), "0000"))
            this.cep = dadosAtualizados.cep;
        this.celular = dadosAtualizados.celular; // verify
    }
    public String getNonNullCpf() {
        if (this.cpf == null) this.cpf = "00000000000";
        return this.cpf;
    }
}
