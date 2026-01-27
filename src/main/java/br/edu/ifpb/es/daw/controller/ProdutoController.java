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

    // GET ALL
    @GetMapping
    public List<Produto> listar() throws PersistenciaDawException {
        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        return dao.getAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id)
            throws PersistenciaDawException {

        ProdutoDAO dao = new ProdutoDAOImpl(emf);
        Produto produto = dao.getByID(id);

        return produto != null
                ? ResponseEntity.ok(produto)
                : ResponseEntity.notFound().build();
    }

    // POST
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

    // PUT
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

    // DELETE
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
