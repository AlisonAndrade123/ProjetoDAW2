package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.CategoriaDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.CategoriaDAOImpl;
import br.edu.ifpb.es.daw.entities.Categoria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para gerenciar categorias de produtos")
public class CategoriaController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    @Operation(summary = "Listar todas as categorias", description = "Retorna uma lista com todas as categorias cadastradas")
    public List<Categoria> listarTodos() throws PersistenciaDawException {
        CategoriaDAO dao = new CategoriaDAOImpl(emf);
        return dao.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID", description = "Retorna os detalhes de uma categoria específica pelo seu ID")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        CategoriaDAO dao = new CategoriaDAOImpl(emf);
        Categoria categoria = dao.getByID(id);
        if (categoria != null) {
            return ResponseEntity.ok(categoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria", description = "Cadastra uma nova categoria no banco de dados")
    public ResponseEntity<Categoria> salvar(@RequestBody Categoria categoria) throws PersistenciaDawException {
        CategoriaDAO dao = new CategoriaDAOImpl(emf);
        try {
            dao.save(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
        } catch (PersistenceException e) {
            System.err.println("Erro ao salvar categoria: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao salvar categoria: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria", description = "Atualiza as informações de uma categoria existente pelo seu ID")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @RequestBody Categoria categoria) throws PersistenciaDawException {
        CategoriaDAO dao = new CategoriaDAOImpl(emf);
        Categoria categoriaExistente = dao.getByID(id);

        if (categoriaExistente != null) {
            categoria.setId(id);
            dao.update(categoria);
            return ResponseEntity.ok(categoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover categoria", description = "Exclui uma categoria do banco de dados pelo seu ID")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        CategoriaDAO dao = new CategoriaDAOImpl(emf);
        Categoria categoriaExistente = dao.getByID(id);

        if (categoriaExistente != null) {
            try {
                dao.delete(id);
                return ResponseEntity.noContent().build();
            } catch (PersistenceException e) {
                System.err.println("Erro ao deletar categoria: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}