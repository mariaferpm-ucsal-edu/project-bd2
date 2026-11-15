package br.edu.bd2.vendas.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Endereco {

    @Column(name = "logradouro", length = 150)
    private String logradouro;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "cidade", length = 80)
    private String cidade;

    @Column(name = "uf", columnDefinition = "char(2)")
    private String uf;

    public Endereco() {
    }

    public Endereco(String logradouro, String numero, String cep, String cidade, String uf) {
        this.logradouro = logradouro;
        this.numero = numero;
        this.cep = cep;
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        return logradouro + ", " + numero + " - " + cidade + "/" + uf;
    }
}
