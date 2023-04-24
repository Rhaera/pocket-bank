package com.github.rhaera.project.pocketbank.model.dto.mongodb;

import com.github.rhaera.project.pocketbank.model.entity.domain.Client;
import com.github.rhaera.project.pocketbank.model.entity.domain.MovimentacaoFinanceira;
import com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria;

import static com.github.rhaera.project.pocketbank.model.entity.domain.implementation.ContaBancaria.TipoConta;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "accounts")
public class ContaObject implements Serializable {

    private Set<String> tipos;
    private String numAgencia;
    @Id private String numConta;
    private Client cliente;
    private LocalDateTime dataCriacao;
    private List<MovimentacaoFinanceira> extrato;
    private BigDecimal saldo;

    public ContaObject toDto(ContaBancaria conta) {
        numAgencia  = conta.getAgencia();
        numConta    = conta.getNumeroConta();
        cliente     = conta.getClient();
        dataCriacao = LocalDateTime.of(conta.getCriacaoDaConta(), LocalTime.now());
        extrato     = conta.getExtrato();
        saldo       = conta.getSaldo();
        tipos       = conta.getTiposDaConta()
                            .stream()
                            .map(TipoConta::toString)
                            .collect(Collectors.toSet());
        return this;
    }

    public ContaObject equalizadorDosTiposDaContaEDadosDoCliente(ContaObject contaAtualizada) throws IOException {
        this.tipos.addAll(contaAtualizada.tipos);
        this.cliente.atualizarDadosViaveis(contaAtualizada.cliente);
        return this;
    }
}
