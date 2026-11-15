package br.edu.bd2.vendas.consulta;

import static br.edu.bd2.vendas.dao.GenericDAO.executarLeitura;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.edu.bd2.vendas.model.Categoria;
import br.edu.bd2.vendas.model.Categoria_;
import br.edu.bd2.vendas.model.Cliente;
import br.edu.bd2.vendas.model.ItemVenda;
import br.edu.bd2.vendas.model.ItemVenda_;
import br.edu.bd2.vendas.model.Loja;
import br.edu.bd2.vendas.model.Loja_;
import br.edu.bd2.vendas.model.Pessoa_;
import br.edu.bd2.vendas.model.Produto;
import br.edu.bd2.vendas.model.Produto_;
import br.edu.bd2.vendas.model.StatusVenda;
import br.edu.bd2.vendas.model.Venda;
import br.edu.bd2.vendas.model.Venda_;

public class ConsultasCriteria {

    @SuppressWarnings("unchecked")
    public List<Venda> pesquisarVendas(FiltroVenda filtro) {
        return executarLeitura(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Venda> cq = cb.createQuery(Venda.class);
            Root<Venda> venda = cq.from(Venda.class);
            Join<Venda, Cliente> cliente = (Join<Venda, Cliente>) venda.fetch(Venda_.cliente);
            Join<Venda, Loja> loja = (Join<Venda, Loja>) venda.fetch(Venda_.loja);

            List<Predicate> predicados = new ArrayList<>();
            if (filtro.getNomeCliente() != null && !filtro.getNomeCliente().trim().isEmpty()) {
                predicados.add(cb.like(cb.lower(cliente.get(Pessoa_.nome)),
                    "%" + filtro.getNomeCliente().trim().toLowerCase() + "%"));
            }
            if (filtro.getLojaId() != null) {
                predicados.add(cb.equal(loja.get(Loja_.id), filtro.getLojaId()));
            }
            if (filtro.getStatus() != null) {
                predicados.add(cb.equal(venda.get(Venda_.status), filtro.getStatus()));
            }
            if (filtro.getInicio() != null) {
                predicados.add(cb.greaterThanOrEqualTo(venda.get(Venda_.dataHora), filtro.getInicio()));
            }
            if (filtro.getFim() != null) {
                predicados.add(cb.lessThanOrEqualTo(venda.get(Venda_.dataHora), filtro.getFim()));
            }

            cq.select(venda)
              .where(predicados.toArray(new Predicate[0]))
              .orderBy(cb.desc(venda.get(Venda_.dataHora)), cb.desc(venda.get(Venda_.id)));
            return em.createQuery(cq).getResultList();
        });
    }

    public List<FaturamentoCategoria> faturamentoPorCategoria(Date inicio, Date fim) {
        return executarLeitura(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<FaturamentoCategoria> cq = cb.createQuery(FaturamentoCategoria.class);
            Root<ItemVenda> item = cq.from(ItemVenda.class);
            Join<ItemVenda, Venda> venda = item.join(ItemVenda_.venda);
            Join<ItemVenda, Produto> produto = item.join(ItemVenda_.produto);
            Join<Produto, Categoria> categoria = produto.join(Produto_.categoria);

            Expression<BigDecimal> valorBruto = cb.prod(item.get(ItemVenda_.precoUnitario),
                cb.toBigDecimal(item.get(ItemVenda_.quantidade)));
            Expression<BigDecimal> valorLiquido = cb.sum(cb.diff(valorBruto, item.get(ItemVenda_.descontoValor)));
            Expression<Long> quantidade = cb.sumAsLong(item.get(ItemVenda_.quantidade));

            cq.select(cb.construct(FaturamentoCategoria.class, categoria.get(Categoria_.nome), quantidade, valorLiquido))
              .where(cb.equal(venda.get(Venda_.status), StatusVenda.CONCLUIDA),
                     cb.between(venda.get(Venda_.dataHora), inicio, fim))
              .groupBy(categoria.get(Categoria_.id), categoria.get(Categoria_.nome))
              .orderBy(cb.desc(valorLiquido));
            return em.createQuery(cq).getResultList();
        });
    }

    public List<Cliente> clientesSemCompraDesde(Date desde) {
        return executarLeitura(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
            Root<Cliente> cliente = cq.from(Cliente.class);

            Subquery<Long> comprasRecentes = cq.subquery(Long.class);
            Root<Venda> venda = comprasRecentes.from(Venda.class);
            comprasRecentes.select(venda.get(Venda_.id))
                .where(cb.equal(venda.get(Venda_.cliente), cliente),
                       cb.notEqual(venda.get(Venda_.status), StatusVenda.CANCELADA),
                       cb.greaterThanOrEqualTo(venda.get(Venda_.dataHora), desde));

            cq.select(cliente)
              .where(cb.not(cb.exists(comprasRecentes)))
              .orderBy(cb.asc(cliente.get(Pessoa_.nome)));
            return em.createQuery(cq).getResultList();
        });
    }
}
