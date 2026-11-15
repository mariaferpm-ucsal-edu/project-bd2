package br.edu.bd2.vendas.consulta;

import java.math.BigDecimal;

public class FaturamentoMensal {

    private final int mes;
    private final long quantidadeVendas;
    private final BigDecimal faturamento;

    public FaturamentoMensal(Integer mes, Long quantidadeVendas, BigDecimal faturamento) {
        this.mes = mes;
        this.quantidadeVendas = quantidadeVendas;
        this.faturamento = faturamento;
    }

    public int getMes() {
        return mes;
    }

    public long getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public BigDecimal getFaturamento() {
        return faturamento;
    }

    @Override
    public String toString() {
        return "Mes " + mes + ": " + quantidadeVendas + " vendas, faturamento " + faturamento;
    }
}
