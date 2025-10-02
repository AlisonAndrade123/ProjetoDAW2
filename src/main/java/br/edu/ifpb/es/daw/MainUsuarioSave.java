package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainUsuarioSave {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            UsuarioDAO dao = new UsuarioDAOImpl(emf);

            System.out.println("Salvando novo usuário...");
            Usuario usuario = new Usuario();
            usuario.setNome("Alison Andrade");
            usuario.setEmail("alison" + System.nanoTime() + "@email.com");
            usuario.setSenha("senhaSuperSecreta123");

            dao.save(usuario);
            System.out.println("Usuário salvo com sucesso! ID: " + usuario.getId());
        }
    }
}