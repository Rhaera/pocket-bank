package com.github.rhaera.project.pocketbank.controller.web.api;

import com.github.rhaera.project.pocketbank.model.ClientDocument;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.ClientForm;
import com.github.rhaera.project.pocketbank.service.implementation.ClientService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients/")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService service;

    @PostMapping("new")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ClientDocument> createClient(@RequestBody @Valid ClientForm newClient, UriComponentsBuilder uribuilder) throws IOException {
        return service.getClientById(newClient.getCpf()).isEmpty() ? ResponseEntity.created(uribuilder.path("/api/v1/clients/new/{account}")
                                                                                    .buildAndExpand(service.postClient(newClient)
                                                                                    .orElseThrow()
                                                                                    .getConta())
                                                                                    .toUri())
                                                                                    .body(service.getClientById(newClient.getCpf()).get()) :
                ResponseEntity.ok(service.getClientById(newClient.getCpf()).get());
    }

    @GetMapping("id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ClientDocument> getById(@RequestParam("id") String cpf) {
        return service.getClientById(cpf).isPresent() ? ResponseEntity.ok(service.getClientById(cpf).get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("all")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<ClientDocument> getAll() {
        return CollectionModel.of(service.getAllClients());
    }

    @PatchMapping("modify/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ClientForm> modifyZipCode(@PathVariable("id") String cpf, @RequestParam("zipCode") String cep) throws IOException {
        return service.getClientById(cpf).isPresent() ? ResponseEntity.ok(service.patchClient(cpf, cep)
                                                                                .orElseThrow()
                                                                                .toNonStaticForm()
                                                                                .toHyperMedia()
                                                                                .add(Link.of(("/api/v1/clients/cpf/")
                                                                                .concat(cpf.trim()
                                                                                .concat("/cep/")
                                                                                .concat(cep))))) : ResponseEntity.noContent().build();
    }

    @PutMapping("update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ClientForm> updateClient(@RequestBody @Valid ClientForm updatedClient) throws IOException {
        return service.getClientById(updatedClient.getCpf()).isEmpty() ? ResponseEntity.noContent().build() :
                (service.putClient(updatedClient).isEmpty() ? ResponseEntity.status(HttpStatus.FORBIDDEN).build() :
                    ResponseEntity.ok(service.getClientById(updatedClient.getCpf())
                                            .get()
                                            .toNonStaticForm()
                                            .toHyperMedia()
                                            .add(Link.of(("/api/v1/clients/cpf/")
                                            .concat(updatedClient.getCpf().trim()
                                            .concat("/cep/")
                                            .concat(updatedClient.getCepDeOrigem()
                                            .toString()))))));
    }

    @DeleteMapping("delete/client")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ClientDocument> deleteUsingId(@RequestParam("id") String cpf) {
        Optional<ClientDocument> deletedClient = service.deleteClientById(cpf);
        return deletedClient.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound()
                                                            .build());
    }

    @DeleteMapping("delete/{zipCode}/all")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<ClientDocument> deleteAllByZipCode(@PathVariable("zipCode") String cep) {
        return CollectionModel.of(service.deletedClientsByZipCode(cep));
    }
}
