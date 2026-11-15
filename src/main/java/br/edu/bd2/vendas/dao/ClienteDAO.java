package br.edu.bd2.vendas.dao;

import java.util.Optional;

import br.edu.bd2.vendas.model.Cliente;

public class ClienteDAO extends GenericDAO<Cliente, Long> {

    public ClienteDAO() {
        super(Cliente.class);
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return executarLeitura(em -> em
            .createQuery("select c from Cliente c where c.cpf = :cpf", Cliente.class)
            .setParameter("cpf", cpf)
            .getResultList()
            .stream()
            .findFirst());
    }
}
