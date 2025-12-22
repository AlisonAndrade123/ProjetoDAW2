package br.edu.ifpb.es.daw.dao;

import br.edu.ifpb.es.daw.entities.Categoria;
import br.edu.ifpb.es.daw.entities.Produto;
import java.util.List;

public interface ProdutoDAO extends DAO<Produto, Long> {

    List<Produto> buscarPorNome(String nome);

    List<Produto> buscarPorCategoria(Categoria categoria);

    List<Produto> buscarPorFaixaDePreco(Double precoMin, Double precoMax);

    Long contarTotalProdutos();
}