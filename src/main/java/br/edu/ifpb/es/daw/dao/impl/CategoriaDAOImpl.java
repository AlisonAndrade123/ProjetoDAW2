package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.entities.Categoria;
import jakarta.persistence.EntityManagerFactory;

public class CategoriaDAOImpl extends AbstractDAOImpl<Categoria, Long> implements CategoriaDAO {

    public CategoriaDAOImpl(EntityManagerFactory emf) {
        super(Categoria.class, emf);
    }
}