package br.edu.bd2.vendas.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Fabrica unica de EntityManagerFactory. Le db.properties e permite sobrescrever
 * por variaveis de ambiente (DB_URL, DB_USER, DB_PASSWORD).
 */
public final class JPAUtil {

    public static final String PERSISTENCE_UNIT = "oltpPU";
    private static EntityManagerFactory emf;

    private JPAUtil() { }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, overrides());
        }
        return emf;
    }

    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    private static Map<String, String> overrides() {
        Properties file = new Properties();
        try (InputStream in = JPAUtil.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                file.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel ler db.properties", e);
        }
        Map<String, String> map = new HashMap<>();
        map.put("javax.persistence.jdbc.url",      pick("DB_URL",      file.getProperty("db.url")));
        map.put("javax.persistence.jdbc.user",     pick("DB_USER",     file.getProperty("db.user")));
        map.put("javax.persistence.jdbc.password", pick("DB_PASSWORD", file.getProperty("db.password")));
        map.values().removeIf(v -> v == null);
        return map;
    }

    private static String pick(String envName, String fallback) {
        String env = System.getenv(envName);
        return (env != null && !env.isEmpty()) ? env : fallback;
    }
}
