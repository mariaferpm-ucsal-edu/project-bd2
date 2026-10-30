package br.edu.bd2.vendas;

import javax.persistence.EntityManager;

import br.edu.bd2.vendas.config.JPAUtil;

/** Teste de fumaca: confirma que Hibernate + PostgreSQL estao funcionando. Rode com: mvn exec:java */
public class App {

    public static void main(String[] args) {
        try {
            EntityManager em = JPAUtil.createEntityManager();
            Object versao = em.createNativeQuery("select version()").getSingleResult();
            System.out.println("Conexao OK -> " + versao);
            em.close();
        } finally {
            JPAUtil.shutdown();
        }
    }
}
