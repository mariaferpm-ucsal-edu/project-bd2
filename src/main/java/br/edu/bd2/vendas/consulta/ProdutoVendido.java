package br.edu.bd2.vendas.consulta;

import java.math.BigDecimal;

public class ProdutoVendido {

    private final String produto;
    private final String laboratorio;
    private final Long quantidade;
    private final BigDecimal faturamento;

    public ProdutoVendido(String produto, String laboratorio, Long quantidade, BigDecimal faturamento) {
        this.produto = produto;
        this.laboratorio = laboratorio;
        this.quantidade = quantidade;
        this.faturamento = faturamento;
    }

    public String getProduto() {
        return produto;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    @Override
    public String toString() {
        return produto + " (" + laboratorio + "): " + quantidade + " unidades, faturamento " + faturamento;
    }
}
