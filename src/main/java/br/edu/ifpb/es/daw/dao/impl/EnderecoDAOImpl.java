package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.EnderecoDAO;
import br.edu.ifpb.es.daw.entities.Endereco;
import jakarta.persistence.EntityManagerFactory;

public class EnderecoDAOImpl extends AbstractDAOImpl<Endereco, Long> implements EnderecoDAO {

    public EnderecoDAOImpl(EntityManagerFactory emf) {
        super(Endereco.class, emf);
    }
}