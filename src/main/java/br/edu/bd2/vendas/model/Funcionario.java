package br.edu.bd2.vendas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "funcionario")
@PrimaryKeyJoinColumn(name = "id")
public class Funcionario extends Pessoa {

    @Column(name = "matricula", length = 30, nullable = false, unique = true)
    private String matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    public Funcionario() {
    }

    public Funcionario(String nome, String cpf, Endereco endereco, String matricula) {
        super(nome, cpf, endereco);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }

    @Override
    public String toString() {
        return "Funcionario[" + getId() + ", " + getNome() + ", " + matricula + "]";
    }
}
