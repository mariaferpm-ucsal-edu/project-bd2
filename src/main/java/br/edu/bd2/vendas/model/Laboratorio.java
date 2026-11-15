package br.edu.bd2.vendas.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "laboratorio")
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 120, nullable = false, unique = true)
    private String nome;

    @OneToMany(mappedBy = "laboratorio")
    private List<Produto> produtos = new ArrayList<>();

    public Laboratorio() {
    }

    public Laboratorio(String nome) {
        this.nome = nome;
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
        produto.setLaboratorio(this);
    }

    public void removerProduto(Produto produto) {
        produtos.remove(produto);
        produto.setLaboratorio(null);
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

    public List<Produto> getProdutos() {
        return produtos;
    }

    @Override
    public String toString() {
        return "Laboratorio[" + id + ", " + nome + "]";
    }
}
