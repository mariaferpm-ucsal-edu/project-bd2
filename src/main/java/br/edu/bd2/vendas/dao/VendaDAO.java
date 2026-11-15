package br.edu.bd2.vendas.dao;

import java.util.Optional;

import br.edu.bd2.vendas.model.StatusVenda;
import br.edu.bd2.vendas.model.Venda;

public class VendaDAO extends GenericDAO<Venda, Long> {

    public VendaDAO() {
        super(Venda.class);
    }

    public Optional<Venda> buscarCompleta(Long id) {
        return executarLeitura(em -> em
            .createQuery("select distinct v from Venda v "
                       + "join fetch v.cliente "
                       + "join fetch v.funcionario "
                       + "join fetch v.loja "
                       + "left join fetch v.notaFiscal "
                       + "left join fetch v.itens i "
                       + "left join fetch i.produto "
                       + "where v.id = :id", Venda.class)
            .setParameter("id", id)
            .getResultList()
            .stream()
            .findFirst());
    }

    public Venda alterarStatus(Long id, StatusVenda status) {
        return executarEmTransacao(em -> {
            Venda venda = em.find(Venda.class, id);
            if (venda == null) {
                throw new IllegalArgumentException("Venda " + id + " nao encontrada");
            }
            venda.setStatus(status);
            return venda;
        });
    }
}
