package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;

public class MainPedidoSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);

            long idUsuarioExistente = 2L;
            long idProdutoExistente = 1L;

            Usuario usuarioAssociado = new Usuario();
            usuarioAssociado.setId(idUsuarioExistente);

            Produto produtoAssociado = new Produto();
            produtoAssociado.setId(idProdutoExistente);

            double precoDoProduto = 29.99;

            Pedido pedido = new Pedido();
            pedido.setDataDoPedido(LocalDateTime.now());
            pedido.setStatus(StatusPedido.ENVIADO);
            pedido.setUsuario(usuarioAssociado); // Associando o usuário "fantasma"

            ItemPedido item = new ItemPedido();
            item.setQuantidade(5);
            item.setPrecoUnitario(precoDoProduto);
            item.setProduto(produtoAssociado); // Associando o produto "fantasma"
            item.setPedido(pedido); // Ligação de volta para o pedido pai (essencial para o relacionamento)

            pedido.getItens().add(item);

            pedido.setValorTotal(item.getQuantidade() * item.getPrecoUnitario());


            pedidoDAO.save(pedido);

            System.out.println("Pedido e seu item salvos com sucesso via cascade!");
            System.out.println("Pedido salvo: " + pedido);
            if (!pedido.getItens().isEmpty()) {
                System.out.println("Item salvo com ID: " + pedido.getItens().get(0).getId());
            }
        }
    }
}