package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.EnderecoDAO;
import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.EnderecoDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainEnderecoSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            EnderecoDAO enderecoDAO = new EnderecoDAOImpl(emf);
            UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);

            List<Usuario> usuarios = usuarioDAO.getAll();
            if (usuarios.isEmpty()) {
                System.err.println("ERRO: Nenhum usuário encontrado. Execute MainUsuarioSave primeiro.");
                return;
            }
            Usuario usuarioAssociado = usuarios.get(0);

            System.out.println("Salvando novo endereço para o usuário: " + usuarioAssociado.getNome());

            Endereco endereco = new Endereco();

            endereco.setRua("Avenida Teste Flexível");
            endereco.setCidade("Cajazeiras");
            endereco.setEstado("PB");
            endereco.setCep("58900-000");

            endereco.setUsuario(usuarioAssociado);

            enderecoDAO.save(endereco);
            System.out.println("Endereço salvo com sucesso!");
        }
    }
}