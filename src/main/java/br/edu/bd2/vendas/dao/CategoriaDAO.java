package br.edu.bd2.vendas.dao;

import java.util.Optional;

import br.edu.bd2.vendas.model.Categoria;

public class CategoriaDAO extends GenericDAO<Categoria, Long> {

    public CategoriaDAO() {
        super(Categoria.class);
    }

    public Optional<Categoria> buscarPorNome(String nome) {
        return executarLeitura(em -> em
            .createQuery("select c from Categoria c where lower(c.nome) = lower(:nome)", Categoria.class)
            .setParameter("nome", nome)
            .getResultList()
            .stream()
            .findFirst());
    }
}
