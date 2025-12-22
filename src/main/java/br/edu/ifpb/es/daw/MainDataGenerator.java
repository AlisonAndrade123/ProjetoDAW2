package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.*;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.entities.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainDataGenerator {
    public static void main(String[] args) throws DawException {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            CategoriaDAO categoriaDAO = new CategoriaDAOImpl(emf);
            ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);

            System.out.println("Gerando dados de teste...");

            Categoria catEletronicos = new Categoria();
            catEletronicos.setNome("Eletrônicos");
            categoriaDAO.save(catEletronicos);

            Categoria catLivros = new Categoria();
            catLivros.setNome("Livros");
            categoriaDAO.save(catLivros);

            criarProduto(produtoDAO, "Notebook Gamer", 5000.00, catEletronicos);
            criarProduto(produtoDAO, "Mouse Sem Fio", 150.00, catEletronicos);
            criarProduto(produtoDAO, "Teclado Mecânico", 300.00, catEletronicos);
            criarProduto(produtoDAO, "Monitor 24pol", 900.00, catEletronicos);

            criarProduto(produtoDAO, "O Senhor dos Anéis", 120.00, catLivros);
            criarProduto(produtoDAO, "Código Limpo", 80.00, catLivros);
            criarProduto(produtoDAO, "Arquitetura Limpa", 90.00, catLivros);

            System.out.println("Dados gerados com sucesso!");
        }
    }

    private static void criarProduto(ProdutoDAO dao, String nome, Double preco, Categoria cat) throws DawException {
        Produto p = new Produto();
        p.setNome(nome);
        p.setPreco(preco);
        p.setQuantidade(10);
        p.setCategoria(cat);
        dao.save(p);
    }
}