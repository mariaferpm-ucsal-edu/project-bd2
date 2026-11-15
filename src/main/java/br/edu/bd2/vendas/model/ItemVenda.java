package br.edu.bd2.vendas.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "item_venda",
       uniqueConstraints = @UniqueConstraint(name = "uk_item_venda_linha", columnNames = {"venda_id", "numero_linha"}))
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "numero_linha", nullable = false)
    private Integer numeroLinha;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "desconto_valor", precision = 12, scale = 2, nullable = false)
    private BigDecimal descontoValor = BigDecimal.ZERO;

    public ItemVenda() {
    }

    public ItemVenda(Produto produto, Integer quantidade, BigDecimal precoUnitario, BigDecimal descontoValor) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.descontoValor = descontoValor == null ? BigDecimal.ZERO : descontoValor;
    }

    public BigDecimal getValorBruto() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public BigDecimal getValorLiquido() {
        return getValorBruto().subtract(descontoValor);
    }

    public Long getId() {
        return id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getDescontoValor() {
        return descontoValor;
    }

    public void setDescontoValor(BigDecimal descontoValor) {
        this.descontoValor = descontoValor;
    }

    @Override
    public String toString() {
        return "ItemVenda[" + id + ", linha " + numeroLinha + ", qtd " + quantidade + ", " + precoUnitario + "]";
    }
}
