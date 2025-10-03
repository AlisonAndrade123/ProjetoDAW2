package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.Pedido;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainPedidoDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PedidoDAO dao = new PedidoDAOImpl(emf);

            List<Pedido> todosOsPedidos = dao.getAll();
            if (todosOsPedidos.isEmpty()) {
                System.out.println("Nenhum pedido para deletar.");
            } else {
                System.out.println("Deletando " + todosOsPedidos.size() + " pedidos...");
                for (Pedido p : todosOsPedidos) {
                    dao.delete(p.getId());
                    System.out.println("Deletado: " + p);
                }
                System.out.println("Todos os pedidos foram deletados com sucesso.");
            }
        }
    }
}