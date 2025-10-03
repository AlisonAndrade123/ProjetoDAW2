package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.ItemPedidoDAO;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import jakarta.persistence.EntityManagerFactory;

public class ItemPedidoDAOImpl extends AbstractDAOImpl<ItemPedido, Long> implements ItemPedidoDAO {

    public ItemPedidoDAOImpl(EntityManagerFactory emf) {
        super(ItemPedido.class, emf);
    }
}