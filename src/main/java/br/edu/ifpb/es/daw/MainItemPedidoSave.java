package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.ItemPedidoDAO;
import br.edu.ifpb.es.daw.dao.impl.ItemPedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainItemPedidoSave {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);

            System.out.println("Salvando novo item de pedido...");
            ItemPedido item = new ItemPedido();
            item.setQuantidade(2);
            item.setPrecoUnitario(49.99);

            dao.save(item);
            System.out.println("Item de pedido salvo com sucesso! ID: " + item.getId());
        }
    }
}