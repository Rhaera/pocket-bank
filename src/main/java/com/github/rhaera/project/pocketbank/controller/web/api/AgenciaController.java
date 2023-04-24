package com.github.rhaera.project.pocketbank.controller.web.api;

import com.github.rhaera.project.pocketbank.model.dto.sql.AgenciaDTO;
import com.github.rhaera.project.pocketbank.model.entity.AgenciaEntity;
import com.github.rhaera.project.pocketbank.service.AgenciaService;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/agencias/")
@Slf4j
public class AgenciaController {

    private final AgenciaService service;

    public AgenciaController(AgenciaService service) {
        this.service = service;
    }

    @PostMapping("send")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AgenciaDTO> postAgency(@RequestBody @Valid AgenciaEntity entity, UriComponentsBuilder uriBuilder) throws IOException {
        return service.createAgency(entity).isPresent() ? ResponseEntity.created(uriBuilder.path("/api/v1/agencias/send/{number}")
                                                                        .buildAndExpand(service.createAgency(entity)
                                                                                                .get()
                                                                                                .getNumber())
                                                                                                .toUri())
                                                                        .body(service.createAgency(entity).get()) :
                ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("read/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> getAgency(@PathVariable("id") Long id) {
        return service.getAgencyById(id).isPresent() ? ResponseEntity.ok(service.getAgencyById(id).get()) :
                ResponseEntity.notFound().build();
    }

    @GetMapping("number/read/{number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> getAgencyByNumber(@PathVariable("number") String number) {
        return service.getAgencyByNumber(number).isPresent() ? ResponseEntity.ok(service.getAgencyByNumber(number)
                                                                        .get()
                                                                        .buildLink()
                                                                        .add(Link.of(("api/v1/agencias/number/").concat(number)))) : ResponseEntity.notFound().build();
    }

    @GetMapping("all")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AgenciaDTO> getAll() {
        return CollectionModel.of(service.getAllAgencies());
    }

    @GetMapping("accounts/read/{accounts}")
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
    public ResponseEntity<AgenciaDTO> updateAmountOfAccounts(@RequestBody @Valid AgenciaEntity entity) {
        return service.updateAgency(entity).isPresent() ? ResponseEntity.ok(service.updateAgency(entity)
                .get()
                .buildLink()
                .add(Link.of(("api/v1/agencias/number/").concat(entity.getNumber())))) : ResponseEntity.notFound().build();
    }

    @PutMapping("update/id")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> updateAmountOfAccountsById(@RequestBody @Valid AgenciaEntity entity, @RequestParam(value = "id") Long id) {
        return service.updateAgencyById(entity, id).isPresent() ? ResponseEntity.ok(service.updateAgencyById(entity, id)
                .get()
                .buildLink()
                .add(Link.of(("api/v1/agencias/number/").concat(entity.getNumber())))) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("delete/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> deleteAgency(@PathVariable(value = "id") Long id) {
        Optional<AgenciaDTO> deleteDTO = service.deleteAgencyById(id);
        return deleteDTO.map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/number/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AgenciaDTO> deleteAgencyByNumber(@RequestParam("number") String number) {
        Optional<AgenciaDTO> deleteDTO = service.deleteAgencyByNumber(number);
        return deleteDTO.map(agenciaDTO -> ResponseEntity.ok(agenciaDTO
                .buildLink()
                .add(Link.of(("api/v1/agencias/number/").concat(number)))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/accounts/delete/{accounts}")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<AgenciaDTO> deleteAgenciesByAmountOfAccounts(@PathVariable("accounts") int accounts) {
        return CollectionModel.of(service.deleteAgenciesByAccounts(accounts));
    }
}
