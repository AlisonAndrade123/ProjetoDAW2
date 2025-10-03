package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.EnderecoDAO;
import br.edu.ifpb.es.daw.dao.impl.EnderecoDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class MainEnderecoDeleteAll {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            EnderecoDAO dao = new EnderecoDAOImpl(emf);

            List<Endereco> todosOsEnderecos = dao.getAll();
            if (todosOsEnderecos.isEmpty()) {
                System.out.println("Nenhum endereço para deletar.");
            } else {
                System.out.println("Deletando " + todosOsEnderecos.size() + " endereços...");
                for (Endereco e : todosOsEnderecos) {
                    dao.delete(e.getId());
                    System.out.println("Deletado: " + e);
                }
                System.out.println("Todos os endereços foram deletados com sucesso.");
            }
        }
    }
}