package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainProdutoSave {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            ProdutoDAO dao = new ProdutoDAOImpl(emf);

            System.out.println("Salvando novo produto...");
            Produto produto = new Produto();
            produto.setNome("Mochila para Notebook " + System.nanoTime());
            produto.setDescricao("Mochila resistente à água, com compartimento para notebook de até 15 polegadas.");
            produto.setPreco(149.90);
            produto.setQuantidade(50);
            produto.setImagemUrl("https://example.com/mochila.jpg");

            dao.save(produto);
            System.out.println("Produto salvo com sucesso! ID: " + produto.getId());
        }
    }
}