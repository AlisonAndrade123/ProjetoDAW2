package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.Pedido;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;

public class MainPedidoSave {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PedidoDAO dao = new PedidoDAOImpl(emf);

            System.out.println("Salvando novo pedido...");
            Pedido pedido = new Pedido();
            pedido.setDataDoPedido(LocalDateTime.now());
            pedido.setStatus("AGUARDANDO_PAGAMENTO");
            pedido.setStatus("AGUARDANDO_PAGAMENTO_" + System.nanoTime());
            pedido.setValorTotal(199.99);

            dao.save(pedido);
            System.out.println("Pedido salvo com sucesso! ID: " + pedido.getId());
        }
    }
}