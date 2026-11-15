package br.edu.bd2.vendas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "venda")
@NamedQuery(name = "Venda.totalPorCliente",
    query = "select new br.edu.bd2.vendas.consulta.TotalPorCliente(c.nome, c.cpf, count(distinct v), "
          + "sum(i.quantidade * i.precoUnitario - i.descontoValor)) "
          + "from Venda v join v.cliente c join v.itens i "
          + "where v.status = :status "
          + "group by c.id, c.nome, c.cpf "
          + "having sum(i.quantidade * i.precoUnitario - i.descontoValor) > :minimo "
          + "order by sum(i.quantidade * i.precoUnitario - i.descontoValor) desc")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_hora", nullable = false)
    private Date dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private StatusVenda status = StatusVenda.ABERTA;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "atualizado_em", nullable = false)
    private Date atualizadoEm;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroLinha")
    private List<ItemVenda> itens = new ArrayList<>();

    @OneToOne(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private NotaFiscal notaFiscal;

    public Venda() {
    }

    public Venda(Cliente cliente, Funcionario funcionario, Loja loja, Date dataHora) {
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.loja = loja;
        this.dataHora = dataHora;
    }

    @PrePersist
    @PreUpdate
    protected void registrarAtualizacao() {
        atualizadoEm = new Date();
        if (dataHora == null) {
            dataHora = atualizadoEm;
        }
    }

    public ItemVenda adicionarItem(Produto produto, int quantidade, BigDecimal descontoValor) {
        ItemVenda item = new ItemVenda(produto, quantidade, produto.getPrecoAtual(), descontoValor);
        item.setNumeroLinha(proximoNumeroLinha());
        item.setVenda(this);
        itens.add(item);
        return item;
    }

    private int proximoNumeroLinha() {
        int maior = 0;
        for (ItemVenda item : itens) {
            if (item.getNumeroLinha() != null && item.getNumeroLinha() > maior) {
                maior = item.getNumeroLinha();
            }
        }
        return maior + 1;
    }

    public void removerItem(ItemVenda item) {
        itens.remove(item);
        item.setVenda(null);
    }

    public NotaFiscal emitirNotaFiscal(String numero, String serie) {
        NotaFiscal nota = new NotaFiscal(numero, serie, new Date());
        nota.setVenda(this);
        this.notaFiscal = nota;
        return nota;
    }

    public void concluir() {
        status = StatusVenda.CONCLUIDA;
    }

    public void cancelar() {
        status = StatusVenda.CANCELADA;
    }

    public BigDecimal getValorBruto() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            total = total.add(item.getValorBruto());
        }
        return total;
    }

    public BigDecimal getValorDesconto() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemVenda item : itens) {
            total = total.add(item.getDescontoValor());
        }
        return total;
    }

    public BigDecimal getValorLiquido() {
        return getValorBruto().subtract(getValorDesconto());
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public NotaFiscal getNotaFiscal() {
        return notaFiscal;
    }

    @Override
    public String toString() {
        return "Venda[" + id + ", " + dataHora + ", " + status + "]";
    }
}
