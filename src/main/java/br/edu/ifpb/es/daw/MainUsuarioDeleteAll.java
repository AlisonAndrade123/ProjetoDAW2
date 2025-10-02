package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainUsuarioDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            UsuarioDAO dao = new UsuarioDAOImpl(emf);

            List<Usuario> todosOsUsuarios = dao.getAll();
            if (todosOsUsuarios.isEmpty()) {
                System.out.println("Nenhum usuário para deletar.");
            } else {
                System.out.println("Deletando " + todosOsUsuarios.size() + " usuários...");
                for (Usuario u : todosOsUsuarios) {
                    dao.delete(u.getId());
                    System.out.println("Deletado: " + u);
                }
                System.out.println("Todos os usuários foram deletados com sucesso.");
            }
        }
    }
}