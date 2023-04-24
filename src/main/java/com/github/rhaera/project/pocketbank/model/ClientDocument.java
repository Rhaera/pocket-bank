package com.github.rhaera.project.pocketbank.model;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.ClientForm;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.io.IOException;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "pocket_bank_clients")
public class ClientDocument {
    private static final ClientDocument DOCUMENT = new ClientDocument();
    @Id
    private String cpf;
    @Field("dataNascimento")
    private LocalDate dataNascimento;
    @Field("cepOrigem")
    private Long cepDeOrigem;
    @Field("numeroAgencia")
    private String agencia;
    @Field("numeroConta")
    private String conta;

    public static ClientDocument toDoc(ClientForm form) throws IOException {
        DOCUMENT.cpf            = form.getCpf();
        DOCUMENT.dataNascimento = form.getDataNascimento();
        DOCUMENT.cepDeOrigem    = form.getCepDeOrigem();
        DOCUMENT.agencia        = UtilLocalizacao.agenciaMaisProxima(form.getCepDeOrigem().toString());
        DOCUMENT.conta          = RequisicaoCadastrarConta.getInstance().getNumeroConta();
        return DOCUMENT;
    }
    public static ClientForm toStaticForm() {
        return new ClientForm(DOCUMENT.cpf, DOCUMENT.dataNascimento, DOCUMENT.cepDeOrigem);
    }
    public ClientForm toNonStaticForm() {
        return new ClientForm(cpf, dataNascimento, cepDeOrigem);
    }
}
