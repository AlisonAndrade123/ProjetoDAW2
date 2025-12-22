package br.edu.ifpb.es.daw.dao.impl;

import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.entities.Categoria;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProdutoDAOImpl extends AbstractDAOImpl<Produto, Long> implements ProdutoDAO {

    public ProdutoDAOImpl(EntityManagerFactory emf) {
        super(Produto.class, emf);
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        var em = getEntityManager();
        String jpql = "SELECT p FROM Produto p WHERE p.nome LIKE :nome";
        TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    @Override
    public List<Produto> buscarPorCategoria(Categoria categoria) {
        var em = getEntityManager();
        String jpql = "SELECT p FROM Produto p WHERE p.categoria = :categoria";
        TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
        query.setParameter("categoria", categoria);
        return query.getResultList();
    }

    @Override
    public List<Produto> buscarPorFaixaDePreco(Double precoMin, Double precoMax) {
        var em = getEntityManager();
        String jpql = "SELECT p FROM Produto p WHERE p.preco BETWEEN :min AND :max";
        TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
        query.setParameter("min", precoMin);
        query.setParameter("max", precoMax);
        return query.getResultList();
    }

    @Override
    public Long contarTotalProdutos() {
        var em = getEntityManager();
        String jpql = "SELECT COUNT(p) FROM Produto p";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        return query.getSingleResult();
    }
}