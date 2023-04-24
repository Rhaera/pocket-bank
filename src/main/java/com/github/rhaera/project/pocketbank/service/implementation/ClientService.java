package com.github.rhaera.project.pocketbank.service.implementation;

import com.github.rhaera.project.pocketbank.model.ClientDocument;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.ClientForm;
import com.github.rhaera.project.pocketbank.model.utility.UtilLocalizacao;

import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ClientService {
    private final MongoTemplate mongoTemplate;

    public Optional<ClientDocument> postClient(ClientForm form) throws IOException {
        return mongoTemplate.exists(new Query().addCriteria(Criteria.where("_id").is(form.getCpf().trim())), "pocket_bank_clients") ?
                Optional.empty() : Optional.of(mongoTemplate.insert(ClientDocument.toDoc(form)));
    }

    public Optional<ClientDocument> getClientById(String cpf) {
        return Optional.ofNullable(mongoTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(cpf.trim())),
                                    ClientDocument.class,
                                    "pocket_bank_clients"));
    }

    public List<ClientDocument> getAllClients() {
        return mongoTemplate.findAll(ClientDocument.class, "pocket_bank_clients");
    }

    public Optional<ClientDocument> patchClient(String cpf, String novoCep) throws IOException {
        if (UtilLocalizacao.agenciaMaisProxima(novoCep.trim()).equals("0000")) return getClientById(cpf.trim());
        mongoTemplate.updateFirst(new Query().addCriteria(Criteria.where("_id").is(cpf.trim())),
                            new Update().set("cepOrigem", novoCep.trim()),
                            ClientDocument.class);
        return getClientById(cpf.trim());
    }

    public Optional<ClientDocument> putClient(ClientForm newForm) throws IOException {
        return !UtilLocalizacao.agenciaMaisProxima(newForm.getCepDeOrigem().toString()).equals("0000") &&
                getClientById(newForm.getCpf()
                                    .trim())
                                    .isPresent() &&
                getClientById(newForm.getCpf()
                                    .trim())
                                    .get()
                                    .getCpf()
                                    .equals(newForm.getCpf()
                                                    .trim()) &&
                getClientById(newForm.getCpf()
                                    .trim())
                                    .get()
                                    .getDataNascimento()
                                    .equals(newForm.getDataNascimento()) ?
                Optional.of(mongoTemplate.save(ClientDocument.toDoc(newForm), "pocket_bank_clients")) :
                Optional.empty();
    }

    public Optional<ClientDocument> deleteClientById(String cpf) {
        if (getClientById(cpf.trim()).isEmpty()) return Optional.empty();
        ClientDocument deletedClient = getClientById(cpf.trim()).get();
        mongoTemplate.remove(getClientById(cpf.trim()).get(), "pocket_bank_clients");
        return Optional.of(deletedClient);
    }

    public List<ClientDocument> deletedClientsByZipCode(String cep) {
        List<ClientDocument> deletedClients = getAllClients().stream()
                                                            .filter(client -> client.getCepDeOrigem()
                                                                                    .toString()
                                                                                    .equals(cep.trim()))
                                                            .collect(Collectors.toList());
        mongoTemplate.findAllAndRemove(new Query().addCriteria(Criteria.where("cepOrigem").is(Long.parseLong(cep.trim()))), ClientDocument.class, "pocket_bank_clients");
        return deletedClients;
    }
}
