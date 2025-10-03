package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.ItemPedidoDAO;
import br.edu.ifpb.es.daw.dao.impl.ItemPedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainItemPedidoDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);

            List<ItemPedido> todosOsItens = dao.getAll();
            if (todosOsItens.isEmpty()) {
                System.out.println("Nenhum item de pedido para deletar.");
            } else {
                System.out.println("Deletando " + todosOsItens.size() + " itens de pedido...");
                for (ItemPedido i : todosOsItens) {
                    dao.delete(i.getId());
                    System.out.println("Deletado: " + i);
                }
                System.out.println("Todos os itens de pedido foram deletados com sucesso.");
            }
        }
    }
}