package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.dao.impl.CategoriaDAOImpl;
import br.edu.ifpb.es.daw.entities.Categoria;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainCategoriaSave {

    public static void main(String[] args) throws DawException {

        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {

            CategoriaDAO dao = new CategoriaDAOImpl(emf);

            System.out.println("Salvando nova categoria...");
            Categoria categoria = new Categoria();
            categoria.setNome("Calçados " + System.nanoTime());

            dao.save(categoria);

            System.out.println("Categoria salva com sucesso! ID: " + categoria.getId());
        }
    }
}