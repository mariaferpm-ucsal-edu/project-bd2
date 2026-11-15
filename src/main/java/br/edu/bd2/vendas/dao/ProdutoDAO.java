package br.edu.bd2.vendas.dao;

import java.util.List;
import java.util.Optional;

import br.edu.bd2.vendas.model.Produto;

public class ProdutoDAO extends GenericDAO<Produto, Long> {

    public ProdutoDAO() {
        super(Produto.class);
    }

    public Optional<Produto> buscarComDetalhes(Long id) {
        return executarLeitura(em -> em
            .createQuery("select p from Produto p "
                       + "join fetch p.categoria "
                       + "join fetch p.laboratorio "
                       + "where p.id = :id", Produto.class)
            .setParameter("id", id)
            .getResultList()
            .stream()
            .findFirst());
    }

    public List<Produto> listarComDetalhes() {
        return executarLeitura(em -> em
            .createQuery("select p from Produto p "
                       + "join fetch p.categoria "
                       + "join fetch p.laboratorio "
                       + "order by p.nome", Produto.class)
            .getResultList());
    }
}
