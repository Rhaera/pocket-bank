package com.github.rhaera.project.pocketbank.controller.web.api;

import com.github.rhaera.project.pocketbank.model.dto.mongodb.RequisicaoCadastrarConta;
import com.github.rhaera.project.pocketbank.model.dto.mongodb.ContaObject;
import com.github.rhaera.project.pocketbank.model.entity.domain.Agencia;
import com.github.rhaera.project.pocketbank.model.mapper.ContaMapper;
import com.github.rhaera.project.pocketbank.service.ContaService;

import jakarta.validation.Valid;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contas/")
@RequiredArgsConstructor
@Slf4j
public class ContaController {

    @NonNull
    private final ContaMapper mapper;

    @NonNull
    private final ContaService service;

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Agencia> publishAgency(@RequestBody @Valid Agencia entity, UriComponentsBuilder uriBuilder) { // pay attention to the links
        return service.addAgency(entity).isPresent() ? ResponseEntity.created(uriBuilder.path("/api/v1/contas/create/{number}")
                                                                                        .buildAndExpand(service.addAgency(entity)
                                                                                                                .get()
                                                                                                                .getCodigoAgencia())
                                                                                                                .toUri())
                                                                                        .body(service.addAgency(entity).get())
                : ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PatchMapping("nova")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RequisicaoCadastrarConta> modifyAgencyList(@RequestBody @Valid ContaObject newAccount) {
        return service.createAccount(newAccount).isPresent() ? ResponseEntity.ok(service.createAccount(newAccount)
                                                                                        .stream()
                                                                                        .map(mapper::toRequest)
                                                                                        .findFirst()
                                                                                        .orElseThrow()
                                                                                        .buildLink()
                                                                                        .add(Link.of(("api/v1/contas/nova/").concat(newAccount.getNumAgencia()
                                                                                                                            .concat("/"))
                                                                                                                            .concat(newAccount.getNumConta())))) :
                ResponseEntity.noContent().build();
    }

    @PatchMapping("{number}/update/conta")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RequisicaoCadastrarConta> modifyAccountByAgency(@PathVariable("number") String number, @RequestBody @Valid ContaObject account)
            throws IOException {
        return service.updateAccountByAgency(number.trim(), account).isPresent() ? ResponseEntity.ok(service.updateAccountByAgency(number.trim(), account)
                                                                                                            .stream()
                                                                                                            .map(mapper::toRequest)
                                                                                                            .findFirst()
                                                                                                            .orElseThrow()
                                                                                                            .buildLink()
                                                                                                            .add(Link.of(("api/v1/contas/{number}/")
                                                                                                                    .concat(account.getNumConta())))) :
                ResponseEntity.noContent().build();
    }

    @PatchMapping("update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RequisicaoCadastrarConta> modifyAccountByClientId(@RequestParam("cpf") String cpf) throws IOException {
        return service.updateAccountByClient(cpf.trim()).isPresent() ? ResponseEntity.ok(service.updateAccountByClient(cpf.trim())
                                                                                                            .stream()
                                                                                                            .map(mapper::toRequest)
                                                                                                            .findFirst()
                                                                                                            .orElseThrow()
                                                                                                            .buildLink()
                                                                                                            .add(Link.of(("api/v1/contas/{cpf}/").concat(cpf.trim())))) :
                ResponseEntity.noContent().build();
    }

    @GetMapping("agencia/{number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RequisicaoCadastrarConta> getAccountByAgency(@PathVariable("number") String agencyNumber, @RequestParam("conta") String account) {
        return service.getBankAccountByAgency(agencyNumber.trim(), account.trim())
                        .isPresent() ? ResponseEntity.ok(service.getBankAccountByAgency(agencyNumber.trim(), account.trim())
                                                                .stream()
                                                                .map(mapper::toRequest)
                                                                .collect(Collectors.toList())
                                                                .get(0)
                                                                .buildLink()
                                                                .add(Link.of(("api/v1/contas/agencia/").concat(account.trim())))) : ResponseEntity.notFound().build();
    }

    @GetMapping("{number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Agencia> getAgencyByCodeNumber(@PathVariable("number") String number) {
        return service.getAgencyById(number).isPresent() ? ResponseEntity.ok(service.getAgencyById(number).get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("agencias/all")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<Agencia> getAllAgenciesPublishedOnDB() {
        return CollectionModel.of(service.getAllAgencies());
    }

    @GetMapping("all/contas")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Map<String, List<ContaObject>>> getAllAccountsMappedByAgencies() {
        return EntityModel.of(service.getAllAccountsGroupingByAgency());
    }

    @GetMapping("all/agencia/contas")
    @ResponseStatus(HttpStatus.OK)
    public CollectionModel<RequisicaoCadastrarConta> getAccountsByAgency(@RequestParam("number") String number) {
        return CollectionModel.of(service.getAllAccountsByAgency(number.trim())
                                        .stream()
                                        .map(mapper::toRequest)
                                        .collect(Collectors.toList()));
    }

    @DeleteMapping("delete/{number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Agencia> deleteAgencyUsingNumber(@PathVariable("number") String number) {
        Optional<Agencia> deletedAgency = service.deleteAgencyById(number.trim());
        return deletedAgency.map(ResponseEntity::ok)
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/agencia/{number}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RequisicaoCadastrarConta> deleteAccountUsingAgencyNumber(@PathVariable("number") String number, @RequestParam("conta") String code) {
        Optional<RequisicaoCadastrarConta> deletedRequest = service.deleteAccountByAgency(number.trim(), code.trim())
                                                                    .stream()
                                                                    .map(mapper::toRequest)
                                                                    .findFirst();
        return deletedRequest.map(request -> ResponseEntity.ok(request
                            .buildLink()
                            .add(Link.of(("api/v1/contas/agencia/")
                                        .concat(number.trim()
                                        .concat("/deleted/")
                                        .concat(code.trim()))))))
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("delete/cliente/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RequisicaoCadastrarConta> deleteAccountUsingClientId(@RequestParam("cpf") String cpf) {
        Optional<RequisicaoCadastrarConta> deletedRequest = service.deleteAccountByClient(cpf.trim())
                                                                    .stream()
                                                                    .map(mapper::toRequest)
                                                                    .findFirst();
        return deletedRequest.map(request -> ResponseEntity.ok(request
                            .buildLink()
                            .add(Link.of(("api/v1/contas/cliente/")
                                        .concat("/deleted/")
                                        .concat(cpf.trim())))))
                            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
