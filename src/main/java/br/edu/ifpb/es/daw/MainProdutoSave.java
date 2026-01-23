package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.CategoriaDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Categoria;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List; // Importar List

public class MainProdutoSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);
            CategoriaDAO categoriaDAO = new CategoriaDAOImpl(emf);

            List<Categoria> categorias = categoriaDAO.getAll();
            if (categorias.isEmpty()) {
                System.err.println("ERRO: Nenhuma categoria encontrada. Execute MainCategoriaSave primeiro.");
                return;
            }
            Categoria categoriaAssociada = categorias.get(0);

            System.out.println("Salvando novo produto e associando à categoria: " + categoriaAssociada.getNome());
            Produto produto = new Produto();
            produto.setNome("Monitor Gamer Ultrawide " + System.nanoTime());
            produto.setPreco(1899.90);
            produto.setCategoria(categoriaAssociada);
            produtoDAO.save(produto);

            System.out.println("Produto salvo com sucesso! ID: " + produto.getId());
        }
    }
}