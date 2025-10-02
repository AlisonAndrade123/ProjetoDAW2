package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.dao.impl.CategoriaDAOImpl;
import br.edu.ifpb.es.daw.entities.Categoria;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainCategoriaDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {

            CategoriaDAO dao = new CategoriaDAOImpl(emf);

            List<Categoria> todasAsCategorias = dao.getAll();

            if (todasAsCategorias.isEmpty()) {
                System.out.println("Nenhuma categoria para deletar.");
            } else {
                System.out.println("Deletando " + todasAsCategorias.size() + " categorias...");
                for (Categoria c : todasAsCategorias) {
                    dao.delete(c.getId());
                    System.out.println("Deletada: " + c);
                }
                System.out.println("Todas as categorias foram deletadas com sucesso.");
            }
        }
    }
}