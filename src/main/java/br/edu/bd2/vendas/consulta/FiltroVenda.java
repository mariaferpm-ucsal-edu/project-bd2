package br.edu.bd2.vendas.consulta;

import java.util.Date;

import br.edu.bd2.vendas.model.StatusVenda;

public class FiltroVenda {

    private String nomeCliente;
    private Long lojaId;
    private StatusVenda status;
    private Date inicio;
    private Date fim;

    public FiltroVenda nomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
        return this;
    }

    public FiltroVenda lojaId(Long lojaId) {
        this.lojaId = lojaId;
        return this;
    }

    public FiltroVenda status(StatusVenda status) {
        this.status = status;
        return this;
    }

    public FiltroVenda inicio(Date inicio) {
        this.inicio = inicio;
        return this;
    }

    public FiltroVenda fim(Date fim) {
        this.fim = fim;
        return this;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public Long getLojaId() {
        return lojaId;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public Date getInicio() {
        return inicio;
    }

    public Date getFim() {
        return fim;
    }
}
