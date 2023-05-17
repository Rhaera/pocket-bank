package com.github.rhaera.project.pocketbank.model.dto.sql;

import com.github.rhaera.project.pocketbank.model.entity.domain.ClientEntity;
import com.github.rhaera.project.pocketbank.model.utility.UtilFormatacoes;
import com.github.rhaera.project.pocketbank.model.utility.ConfirmarSenha;
import com.github.rhaera.project.pocketbank.model.utility.ValidarEmail;

import static com.github.rhaera.project.pocketbank.model.entity.domain.ClientEntity.Gender;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Getter
@Setter
@ConfirmarSenha
public class ClientDTO {
    private final Map<String, String> encryptMap = Map.of("0", "aaa",
                                                            "1", "ace",
                                                            "2", "bdf",
                                                            "3", "gik",
                                                            "4", "hjl",
                                                            "5", "moq",
                                                            "6", "npr",
                                                            "7", "suw",
                                                            "8", "tvx",
                                                            "9", "zzz");
    private final String ADMIN_LIST = "src/main/resources/static/admins.txt";
    @NotNull
    @NotEmpty
    private String cpf;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    @ValidarEmail
    private String email;
    @NotNull
    @NotEmpty
    private String senha;
    @NotNull
    @NotEmpty
    private String confirmarSenha;
    @NotNull
    @NotEmpty
    private String cep;
    @NotNull
    @NotEmpty
    private String dataNascimento;
    @NotNull
    @NotEmpty
    private String celular;
    @NotNull
    @NotEmpty
    private String genero;

    public ClientEntity toEntity() throws FileNotFoundException {
        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setCpf(cpf);
        clientEntity.setName(name);
        clientEntity.usernameFormatter();
        clientEntity.setEmail(email);
        clientEntity.setSenha(senha);
        clientEntity.setCep(cep);
        clientEntity.setDataNascimento(dataNascimento);
        clientEntity.setRole(definingRole());
        clientEntity.setCelular(celular);
        clientEntity.setGenero(genderFormatter());
        return clientEntity;
    }
    private String genderFormatter() {
        return genero.equals("Masculino") ? Gender.MALE.toString() : (genero.equals("Feminino") ? Gender.FEMALE.toString() : Gender.NOT_INFORMED.toString());
    }
    private String definingRole() throws FileNotFoundException {
        try (BufferedReader br = new BufferedReader(new FileReader(ADMIN_LIST))) {
            return br.lines()
                    .reduce("", String::concat).contains(email) ? "ADMIN" : "CLIENT";
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException("PROBLEM WHILE READING FILE!");
        }
    }
    public ClientDTO reverseToDto(String link) {
        cpf            = link.substring(link.indexOf("_0=") + 3, link.indexOf("_1"));
        name           = link.substring(link.indexOf("_1=") + 3, link.indexOf("_2"));
        email          = (link.substring(link.indexOf("_2=") + 3, link.indexOf("_3"))).replace("*", "_");
        senha          = decryptedPassword(link.substring(link.indexOf("_3=") + 3, link.indexOf("_4")));
        cep            = link.substring(link.indexOf("_4=") + 3, link.indexOf("_5"));
        dataNascimento = link.substring(link.indexOf("_5=") + 3, link.indexOf("_6"));
        celular        = link.substring(link.indexOf("_6=") + 3, link.indexOf("_7"));
        genero         = link.substring(link.indexOf("_7=") + 3, link.indexOf("_8"));
        return this;
    }
    private String encryptedPassword() {
        String encryptedPassword = "";
        for (int i = 0; i < senha.length(); i++) {
            encryptedPassword = encryptedPassword.concat(tripleStringFormatterForEncryption(i, encryptMap.get(senha.substring(i, i + 1))));
        }
        return encryptedPassword;
    }
    private String decryptedPassword(String encryptedPassword) {
        String decryptedPassword = "";
        for (int i = 0; i <= 15; i += 3) {
            String encryptedValue = encryptedPassword.substring(i, i + 3);
            decryptedPassword = decryptedPassword.concat(encryptMap.entrySet()
                                                                    .stream()
                                                                    .filter(entry -> entry.getValue()
                                                                            .equals(encryptedValue.toLowerCase()))
                                                                    .map(Map.Entry::getKey)
                                                                    .reduce("", String::concat));
        }
        return decryptedPassword;
    }
    @Override
    public String toString() {
        return "_0=" + cpf + "_".concat("1=" + name + "_"
                .concat("2=" + email.replace("_", "*") + "_"
                        .concat("3=" + encryptedPassword() + "_"
                                .concat("4=" + cep + "_"
                                        .concat("5=" + dataNascimento + "_"
                                                .concat("6=" + celular + "_"
                                                        .concat("7=" + genero + "_8")))))));
    }
    private String tripleStringFormatterForEncryption(int parameter, String secret) {
        String encrypted = "";
        if (parameter % 2 == 0) {
            encrypted = encrypted.concat(secret.substring(0, 1)
                    .concat(secret.substring(1, 2)
                            .toUpperCase()
                            .concat(secret.substring(2))));
            return encrypted;
        }
        encrypted = encrypted.concat(secret.substring(0, 1)
                .toUpperCase()
                .concat(secret.substring(1, 2)
                        .concat(secret.substring(2)
                                .toUpperCase())));
        return encrypted;
    }
}
