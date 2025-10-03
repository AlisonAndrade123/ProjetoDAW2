package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.entities.Pedido;
import jakarta.persistence.EntityManagerFactory;

public class PedidoDAOImpl extends AbstractDAOImpl<Pedido, Long> implements PedidoDAO {

    public PedidoDAOImpl(EntityManagerFactory emf) {
        super(Pedido.class, emf);
    }
}