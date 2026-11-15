package br.edu.bd2.vendas.consulta;

import static br.edu.bd2.vendas.dao.GenericDAO.executarLeitura;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.edu.bd2.vendas.model.Produto;
import br.edu.bd2.vendas.model.StatusVenda;
import br.edu.bd2.vendas.model.Venda;

public class ConsultasJPQL {

    private static final String VALOR_LIQUIDO = "sum(i.quantidade * i.precoUnitario - i.descontoValor)";

    public List<TotalPorCliente> totalPorCliente(BigDecimal minimo) {
        return executarLeitura(em -> em
            .createNamedQuery("Venda.totalPorCliente", TotalPorCliente.class)
            .setParameter("status", StatusVenda.CONCLUIDA)
            .setParameter("minimo", minimo)
            .getResultList());
    }

    public List<Produto> produtosPorCategoria(String categoria) {
        return executarLeitura(em -> em
            .createNamedQuery("Produto.porCategoria", Produto.class)
            .setParameter("categoria", categoria)
            .getResultList());
    }

    public List<Venda> vendasDoClienteNoPeriodo(String cpf, Date inicio, Date fim) {
        return executarLeitura(em -> em
            .createQuery("select v from Venda v join v.cliente c "
                       + "where c.cpf = :cpf and v.dataHora between :inicio and :fim "
                       + "order by v.dataHora", Venda.class)
            .setParameter("cpf", cpf)
            .setParameter("inicio", inicio)
            .setParameter("fim", fim)
            .getResultList());
    }

    public List<Venda> vendasDaLojaComItens(Long lojaId, Date inicio, Date fim) {
        return executarLeitura(em -> em
            .createQuery("select distinct v from Venda v "
                       + "join fetch v.cliente "
                       + "join fetch v.itens i "
                       + "join fetch i.produto "
                       + "where v.loja.id = :lojaId and v.dataHora between :inicio and :fim "
                       + "order by v.dataHora", Venda.class)
            .setParameter("lojaId", lojaId)
            .setParameter("inicio", inicio)
            .setParameter("fim", fim)
            .getResultList());
    }

    public List<FaturamentoLoja> faturamentoPorLoja() {
        return executarLeitura(em -> em
            .createQuery("select new br.edu.bd2.vendas.consulta.FaturamentoLoja("
                       + "l.nome, l.endereco.cidade, count(distinct v), " + VALOR_LIQUIDO + ") "
                       + "from Venda v join v.loja l join v.itens i "
                       + "where v.status = :status "
                       + "group by l.id, l.nome, l.endereco.cidade "
                       + "order by " + VALOR_LIQUIDO + " desc", FaturamentoLoja.class)
            .setParameter("status", StatusVenda.CONCLUIDA)
            .getResultList());
    }

    public List<ProdutoVendido> produtosMaisVendidos(int pagina, int tamanho) {
        return executarLeitura(em -> em
            .createQuery("select new br.edu.bd2.vendas.consulta.ProdutoVendido("
                       + "p.nome, lab.nome, sum(i.quantidade), " + VALOR_LIQUIDO + ") "
                       + "from ItemVenda i join i.produto p join p.laboratorio lab join i.venda v "
                       + "where v.status = :status "
                       + "group by p.id, p.nome, lab.nome "
                       + "order by sum(i.quantidade) desc, p.nome", ProdutoVendido.class)
            .setParameter("status", StatusVenda.CONCLUIDA)
            .setFirstResult(pagina * tamanho)
            .setMaxResults(tamanho)
            .getResultList());
    }

    public List<Produto> produtosNuncaVendidos() {
        return executarLeitura(em -> em
            .createQuery("select p from Produto p "
                       + "where not exists (select i from ItemVenda i where i.produto = p) "
                       + "order by p.nome", Produto.class)
            .getResultList());
    }

    public List<Produto> produtosAcimaDaMediaDaCategoria() {
        return executarLeitura(em -> em
            .createQuery("select p from Produto p join fetch p.categoria c "
                       + "where p.precoAtual > (select avg(p2.precoAtual) from Produto p2 "
                       + "where p2.categoria = p.categoria) "
                       + "order by c.nome, p.precoAtual desc", Produto.class)
            .getResultList());
    }

    public List<FaturamentoMensal> faturamentoMensal(int ano) {
        return executarLeitura(em -> em
            .createQuery("select new br.edu.bd2.vendas.consulta.FaturamentoMensal("
                       + "month(v.dataHora), count(distinct v), " + VALOR_LIQUIDO + ") "
                       + "from Venda v join v.itens i "
                       + "where v.status = :status and year(v.dataHora) = :ano "
                       + "group by month(v.dataHora) "
                       + "order by month(v.dataHora)", FaturamentoMensal.class)
            .setParameter("status", StatusVenda.CONCLUIDA)
            .setParameter("ano", ano)
            .getResultList());
    }

    public List<Venda> listarVendasPaginado(int pagina, int tamanho) {
        return executarLeitura(em -> em
            .createQuery("select v from Venda v join fetch v.cliente join fetch v.loja "
                       + "order by v.dataHora desc, v.id desc", Venda.class)
            .setFirstResult(pagina * tamanho)
            .setMaxResults(tamanho)
            .getResultList());
    }
}
