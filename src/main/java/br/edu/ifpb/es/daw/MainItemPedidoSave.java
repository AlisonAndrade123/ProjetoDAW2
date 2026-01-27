package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.ItemPedidoDAO;
import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.impl.ItemPedidoDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import br.edu.ifpb.es.daw.entities.Pedido;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainItemPedidoSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
            ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(emf);

            long idPedidoExistente = 1L;
            long idProdutoExistente = 1L;

            Pedido pedido = pedidoDAO.getByID(idPedidoExistente);

            if (pedido == null) {
                System.err.println("ERRO: Pedido com ID " + idPedidoExistente + " não encontrado. Crie um primeiro.");
                return;
            }

            System.out.println("Adicionando novo item ao Pedido ID: " + pedido.getId());

            Produto produtoAssociado = new Produto();
            produtoAssociado.setId(idProdutoExistente);
            double precoDoProduto = 15.50; // Preço do novo produto

            ItemPedido novoItem = new ItemPedido();
            novoItem.setQuantidade(1);
            novoItem.setPrecoUnitario(precoDoProduto);
            novoItem.setProduto(produtoAssociado);
            novoItem.setPedido(pedido); // Associando ao pedido que buscamos

            itemPedidoDAO.save(novoItem);

            // (Opcional) Adicionar à lista em memória e atualizar o total do pedido
            pedido.getItens().add(novoItem);

            double novoTotal = pedido.getValorTotal() + (novoItem.getQuantidade() * novoItem.getPrecoUnitario());
            pedido.setValorTotal(novoTotal);

            System.out.println("Novo Item de Pedido salvo com sucesso! ID: " + novoItem.getId());
            System.out.println("Pedido agora tem " + pedido.getItens().size() + " itens.");
        }
    }
}