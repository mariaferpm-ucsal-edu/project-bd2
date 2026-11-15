package br.edu.bd2.vendas.dao;

import java.util.List;
import java.util.Optional;

import br.edu.bd2.vendas.model.Funcionario;

public class FuncionarioDAO extends GenericDAO<Funcionario, Long> {

    public FuncionarioDAO() {
        super(Funcionario.class);
    }

    public Optional<Funcionario> buscarPorMatricula(String matricula) {
        return executarLeitura(em -> em
            .createQuery("select f from Funcionario f join fetch f.loja where f.matricula = :matricula", Funcionario.class)
            .setParameter("matricula", matricula)
            .getResultList()
            .stream()
            .findFirst());
    }

    public List<Funcionario> listarPorLoja(Long lojaId) {
        return executarLeitura(em -> em
            .createQuery("select f from Funcionario f where f.loja.id = :lojaId order by f.nome", Funcionario.class)
            .setParameter("lojaId", lojaId)
            .getResultList());
    }
}
