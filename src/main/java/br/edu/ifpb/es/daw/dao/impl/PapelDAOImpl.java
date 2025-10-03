package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.PapelDAO;
import br.edu.ifpb.es.daw.entities.Papel;
import jakarta.persistence.EntityManagerFactory;

public class PapelDAOImpl extends AbstractDAOImpl<Papel, Long> implements PapelDAO {

    public PapelDAOImpl(EntityManagerFactory emf) {
        super(Papel.class, emf);
    }
}