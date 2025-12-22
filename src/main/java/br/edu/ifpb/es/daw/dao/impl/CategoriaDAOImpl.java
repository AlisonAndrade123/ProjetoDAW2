package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.entities.Categoria;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class CategoriaDAOImpl extends AbstractDAOImpl<Categoria, Long> implements CategoriaDAO {

    public CategoriaDAOImpl(EntityManagerFactory emf) {
        super(Categoria.class, emf);
    }

    @Override
    public Categoria buscarPorIdComProdutos(Long id) {
        var em = getEntityManager();
        // JOIN FETCH força o carregamento da lista
        String jpql = "SELECT c FROM Categoria c LEFT JOIN FETCH c.produtos WHERE c.id = :id";
        TypedQuery<Categoria> query = em.createQuery(jpql, Categoria.class);
        query.setParameter("id", id);

        // Pode retornar null se não encontrar, então tratamos com try/catch ou lista
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}