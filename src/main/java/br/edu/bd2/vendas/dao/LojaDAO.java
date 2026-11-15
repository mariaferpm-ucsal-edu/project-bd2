package br.edu.bd2.vendas.dao;

import java.util.Optional;

import br.edu.bd2.vendas.model.Loja;

public class LojaDAO extends GenericDAO<Loja, Long> {

    public LojaDAO() {
        super(Loja.class);
    }

    public Optional<Loja> buscarPorCnpj(String cnpj) {
        return executarLeitura(em -> em
            .createQuery("select l from Loja l where l.cnpj = :cnpj", Loja.class)
            .setParameter("cnpj", cnpj)
            .getResultList()
            .stream()
            .findFirst());
    }

    public Optional<Loja> buscarComFuncionarios(Long id) {
        return executarLeitura(em -> em
            .createQuery("select distinct l from Loja l left join fetch l.funcionarios where l.id = :id", Loja.class)
            .setParameter("id", id)
            .getResultList()
            .stream()
            .findFirst());
    }
}
