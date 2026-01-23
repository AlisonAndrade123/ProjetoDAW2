package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PapelDAO;
import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.PapelDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import br.edu.ifpb.es.daw.entities.Papel;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List; // Importar List

public class MainUsuarioSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);
            PapelDAO papelDAO = new PapelDAOImpl(emf);
            List<Papel> papeis = papelDAO.getAll();
            if (papeis.isEmpty()) {
                System.err.println("ERRO: Nenhum papel encontrado. Execute MainPapelSave primeiro.");
                return;
            }
            Papel papelAssociado = papeis.get(0);

            System.out.println("Salvando novo usuário e associando ao papel: " + papelAssociado.getNome());
            Usuario usuario = new Usuario();
            usuario.setNome("Usuário com Papel " + System.nanoTime());
            usuario.setEmail("usuario.papel" + System.nanoTime() + "@email.com");
            usuario.setSenha("senha123");

            usuario.getPapeis().add(papelAssociado);

            Endereco endereco = new Endereco();
            endereco.setRua("Rua da Cascata");
            endereco.setCidade("Sousa");
            endereco.setCep("58700-123");
            endereco.setEstado("PB");

            endereco.setUsuario(usuario);
            usuario.getEnderecos().add(endereco);

            usuarioDAO.save(usuario);
            System.out.println("Usuário salvo com sucesso!");
        }
    }
}