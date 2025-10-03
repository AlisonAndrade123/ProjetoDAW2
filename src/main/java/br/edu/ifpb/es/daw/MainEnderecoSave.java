package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.EnderecoDAO;
import br.edu.ifpb.es.daw.dao.impl.EnderecoDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainEnderecoSave {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            EnderecoDAO dao = new EnderecoDAOImpl(emf);

            System.out.println("Salvando novo endereço...");
            Endereco endereco = new Endereco();
            endereco.setRua("Rua da Universidade");
            endereco.setNumero("123");
            endereco.setComplemento("Bloco C, Apto 404");
            endereco.setBairro("Centro");
            endereco.setCidade("João Pessoa");
            endereco.setEstado("PB");
            endereco.setCep("58000-" + System.nanoTime());

            dao.save(endereco);
            System.out.println("Endereço salvo com sucesso! ID: " + endereco.getId());
        }
    }
}