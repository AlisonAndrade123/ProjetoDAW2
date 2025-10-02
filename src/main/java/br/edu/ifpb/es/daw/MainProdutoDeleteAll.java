package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainProdutoDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            ProdutoDAO dao = new ProdutoDAOImpl(emf);

            List<Produto> todosOsProdutos = dao.getAll();
            if (todosOsProdutos.isEmpty()) {
                System.out.println("Nenhum produto para deletar.");
            } else {
                System.out.println("Deletando " + todosOsProdutos.size() + " produtos...");
                for (Produto p : todosOsProdutos) {
                    dao.delete(p.getId());
                    System.out.println("Deletado: " + p);
                }
                System.out.println("Todos os produtos foram deletados com sucesso.");
            }
        }
    }
}