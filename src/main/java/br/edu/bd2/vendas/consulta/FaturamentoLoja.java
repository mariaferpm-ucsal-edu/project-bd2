package br.edu.bd2.vendas.consulta;

import java.math.BigDecimal;

public class FaturamentoLoja {

    private final String loja;
    private final String cidade;
    private final Long quantidadeVendas;
    private final BigDecimal faturamento;

    public FaturamentoLoja(String loja, String cidade, Long quantidadeVendas, BigDecimal faturamento) {
        this.loja = loja;
        this.cidade = cidade;
        this.quantidadeVendas = quantidadeVendas;
        this.faturamento = faturamento;
    }

    public String getLoja() {
        return loja;
    }

    public String getCidade() {
        return cidade;
    }

    public Long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    @Override
    public String toString() {
        return loja + " - " + cidade + ": " + quantidadeVendas + " vendas, faturamento " + faturamento;
    }
}
