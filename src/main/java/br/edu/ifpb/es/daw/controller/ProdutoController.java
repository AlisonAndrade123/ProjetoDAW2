package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private EntityManagerFactory emf;

    // ================== GET NORMAL ==================
    @GetMapping
    public List<Produto> listar() throws PersistenciaDawException {
        return new ProdutoDAOImpl(emf).getAll();
    }

    // ================== GET PAGINAÇÃO ==================
    @GetMapping("/page")
    public ResponseEntity<List<Produto>> listarComPaginacao(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        List<Produto> todos = dao.getAll();

        int start = page * size;
        int end = Math.min(start + size, todos.size());

        if (start >= todos.size()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(todos.subList(start, end));
    }

    // ================== GET FILTRO ==================
    @GetMapping("/filtro")
    public ResponseEntity<List<Produto>> filtrar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId
    ) throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        List<Produto> todos = dao.getAll();

        List<Produto> filtrados = todos.stream()
                .filter(p ->
                        (nome == null || p.getNome().toLowerCase().contains(nome.toLowerCase())) &&
                                (categoriaId == null || p.getCategoria().getId().equals(categoriaId))
                )
                .toList();

        return ResponseEntity.ok(filtrados);
    }

    // ================== GET POR ID ==================
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id)
            throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        Produto produto = dao.getByID(id);

        return produto != null
                ? ResponseEntity.ok(produto)
                : ResponseEntity.notFound().build();
    }

    // ================== POST ==================
    @PostMapping
    public ResponseEntity<Produto> salvar(@RequestBody Produto produto)
            throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);

        try {
            dao.save(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produto);
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // ================== PUT ==================
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestBody Produto produto) throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        Produto existente = dao.getByID(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        produto.setId(id);
        dao.update(produto);

        return ResponseEntity.ok(produto);
    }

    // ================== DELETE ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id)
            throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        Produto existente = dao.getByID(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            dao.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
