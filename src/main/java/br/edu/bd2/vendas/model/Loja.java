package br.edu.bd2.vendas.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "loja")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 120, nullable = false)
    private String nome;

    @Column(name = "cnpj", length = 14, nullable = false, unique = true)
    private String cnpj;

    @Embedded
    private Endereco endereco;

    @OneToMany(mappedBy = "loja")
    private List<Funcionario> funcionarios = new ArrayList<>();

    public Loja() {
    }

    public Loja(String nome, String cnpj, Endereco endereco) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
        funcionario.setLoja(this);
    }

    public void removerFuncionario(Funcionario funcionario) {
        funcionarios.remove(funcionario);
        funcionario.setLoja(null);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    @Override
    public String toString() {
        return "Loja[" + id + ", " + nome + ", " + cnpj + "]";
    }
}
