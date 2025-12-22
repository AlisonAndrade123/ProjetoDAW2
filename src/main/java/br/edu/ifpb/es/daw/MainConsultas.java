package br.edu.ifpb.es.daw;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.CategoriaDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Categoria;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class MainConsultas {
    public static void main(String[] args) throws DawException {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("daw")) {
            ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);
            CategoriaDAO categoriaDAO = new CategoriaDAOImpl(emf);

            System.out.println("=== EXECUTANDO CONSULTAS JPQL ===\n");

            System.out.println("--- 2.1 Buscar Produtos com 'Gamer' no nome ---");
            List<Produto> gamers = produtoDAO.buscarPorNome("Gamer");
            gamers.forEach(p -> System.out.println(p.getNome() + " - R$ " + p.getPreco()));

            System.out.println("\n--- 2.2 Buscar Produtos da Categoria 'Livros' ---");
            // Primeiro precisamos pegar a entidade Categoria do banco para passar como parâmetro
            List<Categoria> todasCategorias = categoriaDAO.getAll();
            Categoria catLivros = todasCategorias.stream()
                    .filter(c -> c.getNome().equals("Livros"))
                    .findFirst()
                    .orElse(null);

            if (catLivros != null) {
                List<Produto> livros = produtoDAO.buscarPorCategoria(catLivros);
                livros.forEach(p -> System.out.println(p.getNome()));
            }

            System.out.println("\n--- 2.3 Buscar Produtos entre R$ 100,00 e R$ 500,00 ---");
            List<Produto> baratos = produtoDAO.buscarPorFaixaDePreco(100.0, 500.0);
            baratos.forEach(p -> System.out.println(p.getNome() + " - R$ " + p.getPreco()));

            System.out.println("\n--- 2.4 Total de Produtos Cadastrados ---");
            Long total = produtoDAO.contarTotalProdutos();
            System.out.println("Total: " + total);

            System.out.println("\n--- 2.5 Buscar Categoria 'Eletrônicos' carregando Produtos (FETCH) ---");
            Categoria catEletronicosRef = todasCategorias.stream()
                    .filter(c -> c.getNome().equals("Eletrônicos"))
                    .findFirst()
                    .orElse(null);

            if (catEletronicosRef != null) {
                Categoria catComProdutos = categoriaDAO.buscarPorIdComProdutos(catEletronicosRef.getId());
                System.out.println("Categoria: " + catComProdutos.getNome());
                System.out.println("Produtos carregados:");
                // Se não tivesse usado FETCH, e a sessão estivesse fechada, esta linha daria erro
                catComProdutos.getProdutos().forEach(p -> System.out.println(" - " + p.getNome()));
            }
        }
    }
}