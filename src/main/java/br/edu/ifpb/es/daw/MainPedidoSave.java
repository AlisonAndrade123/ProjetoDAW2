package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.*;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class MainPedidoSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);
            ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);

            List<Usuario> usuarios = usuarioDAO.getAll();
            List<Produto> produtos = produtoDAO.getAll();

            if (usuarios.isEmpty() || produtos.isEmpty()) {
                System.err.println("ERRO: É preciso ter pelo menos um usuário e um produto cadastrados.");
                return;
            }
            Usuario usuarioAssociado = usuarios.get(0);
            Produto produtoAssociado = produtos.get(0);


            System.out.println("Criando pedido para " + usuarioAssociado.getNome() + " com o produto " + produtoAssociado.getNome());
            Pedido pedido = new Pedido();
            pedido.setDataDoPedido(LocalDateTime.now());
            pedido.setStatus(StatusPedido.ENVIADO);
            pedido.setUsuario(usuarioAssociado);

            ItemPedido item = new ItemPedido();
            item.setQuantidade(1);
            item.setPrecoUnitario(produtoAssociado.getPreco());
            item.setProduto(produtoAssociado);
            item.setPedido(pedido);

            pedido.getItens().add(item);
            pedido.setValorTotal(item.getQuantidade() * item.getPrecoUnitario());

            pedidoDAO.save(pedido);
            System.out.println("Pedido salvo com sucesso!");
        }
    }
}