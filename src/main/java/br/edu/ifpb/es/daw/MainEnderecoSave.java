package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.EnderecoDAO;
import br.edu.ifpb.es.daw.dao.impl.EnderecoDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import br.edu.ifpb.es.daw.entities.Usuario;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainEnderecoSave {
    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            EnderecoDAO enderecoDAO = new EnderecoDAOImpl(emf);

            long idUsuarioExistente = 2L;

            // Cria o "Usuário fantasma"
            Usuario usuarioAssociado = new Usuario();
            usuarioAssociado.setId(idUsuarioExistente);


            Endereco endereco = new Endereco();
            endereco.setRua("Avenida Teste de Associação");
            endereco.setCidade("Cajazeiras");
            endereco.setEstado("PB");
            endereco.setCep("58900-000");
            endereco.setUsuario(usuarioAssociado);

            enderecoDAO.save(endereco);
            System.out.println("Endereço salvo com sucesso, associado ao Usuário ID: " + idUsuarioExistente);
        }
    }
}