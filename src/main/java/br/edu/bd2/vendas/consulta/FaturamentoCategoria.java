package br.edu.bd2.vendas.consulta;

import java.math.BigDecimal;

public class FaturamentoCategoria {

    private final String categoria;
    private final Long quantidade;
    private final BigDecimal faturamento;

    public FaturamentoCategoria(String categoria, Long quantidade, BigDecimal faturamento) {
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.faturamento = faturamento;
    }

    public String getCategoria() {
        return categoria;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    @Override
    public String toString() {
        return categoria + ": " + quantidade + " unidades, faturamento " + faturamento;
    }
}
