package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.*;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainDeleteAll {
    public static void main(String[] args) throws DawException {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {

            PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
            List<Pedido> pedidos = pedidoDAO.getAll();
            for (Pedido p : pedidos) {
                pedidoDAO.delete(p.getId());
            }

            ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);
            List<Produto> produtos = produtoDAO.getAll();
            for (Produto p : produtos) {
                produtoDAO.delete(p.getId());
            }

            CategoriaDAO categoriaDAO = new CategoriaDAOImpl(emf);
            List<Categoria> categorias = categoriaDAO.getAll();
            for (Categoria c : categorias) {
                categoriaDAO.delete(c.getId());
            }

            System.out.println("Banco de dados limpo!");
        }
    }
}