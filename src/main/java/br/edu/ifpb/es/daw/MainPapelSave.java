package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PapelDAO;
import br.edu.ifpb.es.daw.dao.impl.PapelDAOImpl;
import br.edu.ifpb.es.daw.entities.Papel;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainPapelSave {

    public static void main(String[] args) throws DawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PapelDAO dao = new PapelDAOImpl(emf);

            System.out.println("Salvando novos papéis...");

            Papel papelCliente = new Papel();

            papelCliente.setNome("ROLE_CLIENTE_" + System.nanoTime());
            dao.save(papelCliente);
            System.out.println("Papel salvo com sucesso! " + papelCliente);

            Papel papelAdmin = new Papel();

            papelAdmin.setNome("ROLE_ADMIN_" + System.nanoTime());
            dao.save(papelAdmin);
            System.out.println("Papel salvo com sucesso! " + papelAdmin);
        }
    }
}