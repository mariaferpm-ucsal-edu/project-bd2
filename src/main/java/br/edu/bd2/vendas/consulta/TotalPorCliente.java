package br.edu.bd2.vendas.consulta;

import java.math.BigDecimal;

public class TotalPorCliente {

    private final String nome;
    private final String cpf;
    private final Long quantidadeVendas;
    private final BigDecimal total;

    public TotalPorCliente(String nome, String cpf, Long quantidadeVendas, BigDecimal total) {
        this.nome = nome;
        this.cpf = cpf;
        this.quantidadeVendas = quantidadeVendas;
        this.total = total;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public Long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public BigDecimal getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return nome + " (" + cpf + "): " + quantidadeVendas + " vendas, total " + total;
    }
}
