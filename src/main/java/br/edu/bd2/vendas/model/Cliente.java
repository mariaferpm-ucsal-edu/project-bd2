package br.edu.bd2.vendas.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Pessoa {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "cadastrado_em", nullable = false, updatable = false)
    private Date cadastradoEm;

    public Cliente() {
    }

    public Cliente(String nome, String cpf, Endereco endereco) {
        super(nome, cpf, endereco);
    }

    @Override
    protected void registrarAtualizacao() {
        super.registrarAtualizacao();
        if (cadastradoEm == null) {
            cadastradoEm = getAtualizadoEm();
        }
    }

    public Date getCadastradoEm() {
        return cadastradoEm;
    }

    public void setCadastradoEm(Date cadastradoEm) {
        this.cadastradoEm = cadastradoEm;
    }

    @Override
    public String toString() {
        return "Cliente[" + getId() + ", " + getNome() + ", " + getCpf() + "]";
    }
}
