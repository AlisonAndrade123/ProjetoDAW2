package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.PapelDAO;
import br.edu.ifpb.es.daw.dao.impl.PapelDAOImpl;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.entities.NomePapel; // Importe o enum NomePapel
import br.edu.ifpb.es.daw.entities.Papel;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainPapelSave {

    public static void main(String[] args) throws PersistenciaDawException {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            PapelDAO dao = new PapelDAOImpl(emf);

            System.out.println("Salvando novos papéis...");

            // --- Salvar Papel Cliente ---
            Papel papelCliente = new Papel();
            // CORREÇÃO: Usando NomePapel.CLIENTE (sem o "ROLE_")
            papelCliente.setNome(NomePapel.CLIENTE);
            dao.save(papelCliente);
            System.out.println("Papel salvo com sucesso! " + papelCliente);

            // --- Salvar Papel Admin ---
            Papel papelAdmin = new Papel();
            // CORREÇÃO: Usando NomePapel.ADMIN (sem o "ROLE_")
            papelAdmin.setNome(NomePapel.ADMIN);
            dao.save(papelAdmin);
            System.out.println("Papel salvo com sucesso! " + papelAdmin);
        }
    }
}