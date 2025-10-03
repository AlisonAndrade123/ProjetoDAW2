package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PapelDAO;
import br.edu.ifpb.es.daw.dao.impl.PapelDAOImpl;
import br.edu.ifpb.es.daw.entities.Papel;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainPapelDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PapelDAO dao = new PapelDAOImpl(emf);

            List<Papel> todosOsPapeis = dao.getAll();
            if (todosOsPapeis.isEmpty()) {
                System.out.println("Nenhum papel para deletar.");
            } else {
                System.out.println("Deletando " + todosOsPapeis.size() + " papéis...");
                for (Papel p : todosOsPapeis) {
                    dao.delete(p.getId());
                    System.out.println("Deletado: " + p);
                }
                System.out.println("Todos os papéis foram deletados com sucesso.");
            }
        }
    }
}