package br.edu.bd2.vendas.dao;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.edu.bd2.vendas.config.JPAUtil;

/**
 * DAO generico com CRUD e controle de transacao.
 * Cada operacao abre seu proprio EntityManager e transacao (modelo simples, adequado ao trabalho).
 */
public class GenericDAO<T, ID extends Serializable> {

    private final Class<T> classe;

    public GenericDAO(Class<T> classe) {
        this.classe = classe;
    }

    /** Executa um trecho dentro de uma transacao (commit ou rollback automatico). */
    public static <R> R executarEmTransacao(Function<EntityManager, R> acao) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R resultado = acao.apply(em);
            tx.commit();
            return resultado;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /** Executa uma leitura (sem transacao explicita). */
    public static <R> R executarLeitura(Function<EntityManager, R> acao) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return acao.apply(em);
        } finally {
            em.close();
        }
    }

    public T inserir(T entidade) {
        return executarEmTransacao(em -> { em.persist(entidade); return entidade; });
    }

    public T atualizar(T entidade) {
        return executarEmTransacao(em -> em.merge(entidade));
    }

    public void remover(ID id) {
        executarEmTransacao(em -> {
            T alvo = em.find(classe, id);
            if (alvo != null) {
                em.remove(alvo);
            }
            return null;
        });
    }

    public T buscarPorId(ID id) {
        return executarLeitura(em -> em.find(classe, id));
    }

    public List<T> listarTodos() {
        return executarLeitura(em ->
            em.createQuery("select e from " + classe.getSimpleName() + " e", classe).getResultList());
    }

    public long contar() {
        return executarLeitura(em ->
            em.createQuery("select count(e) from " + classe.getSimpleName() + " e", Long.class)
              .getSingleResult());
    }
}
