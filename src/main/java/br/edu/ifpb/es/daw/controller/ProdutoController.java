package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private EntityManagerFactory emf;

    // ✅ GET /api/produtos?page=0&size=10&nome=monitor&categoriaId=1&minPreco=100&maxPreco=2000&sort=preco,desc
    @GetMapping
    public ResponseEntity<List<Produto>> listar(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Double minPreco,
            @RequestParam(required = false) Double maxPreco,
            @RequestParam(required = false) String sort
    ) throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        List<Produto> produtos = dao.getAll();

        // filtros
        if (nome != null && !nome.isBlank()) {
            String n = nome.toLowerCase();
            produtos = produtos.stream()
                    .filter(p -> p.getNome() != null && p.getNome().toLowerCase().contains(n))
                    .collect(Collectors.toList());
        }

        if (categoriaId != null) {
            produtos = produtos.stream()
                    .filter(p -> p.getCategoria() != null && categoriaId.equals(p.getCategoria().getId()))
                    .collect(Collectors.toList());
        }

        if (minPreco != null) {
            produtos = produtos.stream()
                    .filter(p -> p.getPreco() != null && p.getPreco() >= minPreco)
                    .collect(Collectors.toList());
        }

        if (maxPreco != null) {
            produtos = produtos.stream()
                    .filter(p -> p.getPreco() != null && p.getPreco() <= maxPreco)
                    .collect(Collectors.toList());
        }

        // sort simples: sort=preco,asc | sort=preco,desc | sort=nome,asc | sort=nome,desc
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String campo = parts[0].trim();
            String direcao = parts.length > 1 ? parts[1].trim().toLowerCase() : "asc";

            Comparator<Produto> comp = null;

            if ("preco".equalsIgnoreCase(campo)) {
                comp = Comparator.comparing(Produto::getPreco, Comparator.nullsLast(Double::compareTo));
            } else if ("nome".equalsIgnoreCase(campo)) {
                comp = Comparator.comparing(Produto::getNome, Comparator.nullsLast(String::compareToIgnoreCase));
            } else if ("quantidade".equalsIgnoreCase(campo)) {
                comp = Comparator.comparing(Produto::getQuantidade, Comparator.nullsLast(Integer::compareTo));
            }

            if (comp != null) {
                if ("desc".equals(direcao)) comp = comp.reversed();
                produtos = produtos.stream().sorted(comp).collect(Collectors.toList());
            }
        }

        // paginação
        int from = Math.max(0, page * size);
        int to = Math.min(produtos.size(), from + size);
        if (from > produtos.size()) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(produtos.subList(from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        Produto produto = dao.getByID(id);
        return (produto != null) ? ResponseEntity.ok(produto) : ResponseEntity.notFound().build();
    }
}
