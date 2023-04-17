package com.github.rhaera.project.pocketbank.controller;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.service.AgenciaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/agencias/")
public class AgenciaController {
    @Autowired
    private AgenciaService service;

    @PostMapping("send")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AgenciaDTO> postAgency(@RequestBody @Valid AgenciaDTO dto, UriComponentsBuilder uriBuilder) throws IOException {
        return ResponseEntity.created(uriBuilder.path("api/v1/agencias/send/{number}")
                        .buildAndExpand(service.createAgency(dto.toEntity())
                                .orElse(service.getAgencyByNumber(dto.getNumber())
                                        .orElse(dto))
                                .getNumber())
                        .toUri())
                .body(service.createAgency(dto.toEntity())
                        .orElseThrow(() -> new IOException("Falha na inserção da nova agência!")));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> getAgency(@PathVariable("id") Long id) {
        return ResponseEntity.of(service.getAgencyById(id));
    }

    @GetMapping("number/{number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> getAgencyByNumber(@PathVariable("number") String number) {
        return ResponseEntity.ok(service.getAgencyByNumber(number)
                .stream()
                .collect(Collectors.toList())
                .get(0)
                .buildLink()
                .add(Link.of(("api/v1/agencias/number/").concat(number))));
    }

    @GetMapping("all")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AgenciaDTO> getAll() {
        return CollectionModel.of(service.getAllAgencies());
    }

    @GetMapping("accounts/{accounts}")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AgenciaDTO> getAgenciesWithSameAccountAmount(@PathVariable("accounts") int accounts) {
        return CollectionModel.of(service.getAgenciesByAccounts(accounts));
    }

    @GetMapping("accounts")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AgenciaDTO> getAgenciesWithSameAmountOfAccounts(@RequestParam int accounts) {
        return CollectionModel.of(service.getAgenciesByAccounts(accounts));
    }

    @PutMapping("update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> updateAmountOfAccounts(@RequestBody @Valid AgenciaDTO dto) {
        return ResponseEntity.ok(service.updateAgency(dto.toEntity())
                .stream()
                .collect(Collectors.toList())
                .get(0));
    }

    @PutMapping("update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> updateAmountOfAccountsById(@RequestBody @Valid AgenciaDTO dto, @RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(service.updateAgencyById(dto.toEntity(), id)
                .stream()
                .collect(Collectors.toList())
                .get(0));
    }

    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> deleteAgency(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(service.deleteAgencyById(id)
                .stream()
                .collect(Collectors.toList())
                .get(0));
    }

    @DeleteMapping("delete/number")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> deleteAgencyByNumber(@RequestParam("number") String number) {
        return ResponseEntity.ok(service.deleteAgencyByNumber(number)
                .stream()
                .collect(Collectors.toList())
                .get(0));
    }

    @DeleteMapping("delete/accounts/{accounts}")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AgenciaDTO> deleteAgenciesByAmountOfAccounts(@PathVariable("accounts") int accounts) {
        return CollectionModel.of(service.deleteAgenciesByAccounts(accounts));
    }
}
