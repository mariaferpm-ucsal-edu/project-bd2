package br.edu.bd2.vendas.dao;

import java.util.Optional;

import br.edu.bd2.vendas.model.Laboratorio;

public class LaboratorioDAO extends GenericDAO<Laboratorio, Long> {

    public LaboratorioDAO() {
        super(Laboratorio.class);
    }

    public Optional<Laboratorio> buscarPorNome(String nome) {
        return executarLeitura(em -> em
            .createQuery("select l from Laboratorio l where lower(l.nome) = lower(:nome)", Laboratorio.class)
            .setParameter("nome", nome)
            .getResultList()
            .stream()
            .findFirst());
    }
}
