package br.edu.bd2.vendas;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Test;

import br.edu.bd2.vendas.config.JPAUtil;

/** Requer o PostgreSQL de pe (docker compose up -d). */
public class ConexaoTest {

    @AfterClass
    public static void fechar() {
        JPAUtil.shutdown();
    }

    @Test
    public void deveConectarNoBanco() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            assertNotNull(em.createNativeQuery("select 1").getSingleResult());
        } finally {
            em.close();
        }
    }
}
