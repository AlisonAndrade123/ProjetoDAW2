package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import br.edu.ifpb.es.daw.entities.Papel;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainUsuarioSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);

            long idPapelExistente = 1L;

            Papel papelAssociado = new Papel();
            papelAssociado.setId(idPapelExistente);

            System.out.println("Salvando novo usuário com associações...");
            Usuario usuario = new Usuario();
            usuario.setNome("Usuário Final com Papel " + System.nanoTime());
            usuario.setEmail("usuario.final.papel" + System.nanoTime() + "@email.com");
            usuario.setSenha("senhaForte123");

            usuario.getPapeis().add(papelAssociado);

            Endereco endereco = new Endereco();
            endereco.setRua("Rua da Associação Final");
            endereco.setCidade("Patos");
            endereco.setEstado("PB");
            endereco.setCep("58700-123");
            endereco.setUsuario(usuario);
            usuario.getEnderecos().add(endereco);

            usuarioDAO.save(usuario);

            System.out.println("Usuário salvo com sucesso! ID: " + usuario.getId());
            if (!usuario.getEnderecos().isEmpty()) {
                System.out.println("Endereço salvo via cascade com ID: " + usuario.getEnderecos().get(0).getId());
            }
        }
    }
}