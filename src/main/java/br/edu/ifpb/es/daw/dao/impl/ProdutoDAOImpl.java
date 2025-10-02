package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;

public class ProdutoDAOImpl extends AbstractDAOImpl<Produto, Long> implements ProdutoDAO {

    public ProdutoDAOImpl(EntityManagerFactory emf) {
        super(Produto.class, emf);
    }
}