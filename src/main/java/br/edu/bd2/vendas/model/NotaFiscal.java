package br.edu.bd2.vendas.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "nota_fiscal",
       uniqueConstraints = @UniqueConstraint(name = "uk_nota_fiscal_numero_serie", columnNames = {"numero", "serie"}))
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false, unique = true)
    private Venda venda;

    @Column(name = "numero", length = 20, nullable = false)
    private String numero;

    @Column(name = "serie", length = 10, nullable = false)
    private String serie;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "emitida_em", nullable = false)
    private Date emitidaEm;

    public NotaFiscal() {
    }

    public NotaFiscal(String numero, String serie, Date emitidaEm) {
        this.numero = numero;
        this.serie = serie;
        this.emitidaEm = emitidaEm;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getEmitidaEm() {
        return emitidaEm;
    }

    public void setEmitidaEm(Date emitidaEm) {
        this.emitidaEm = emitidaEm;
    }

    @Override
    public String toString() {
        return "NotaFiscal[" + id + ", " + serie + "-" + numero + "]";
    }
}
