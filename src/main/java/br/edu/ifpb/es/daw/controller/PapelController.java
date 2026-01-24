package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PapelDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.PapelDAOImpl;
import br.edu.ifpb.es.daw.entities.Papel;
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
@RequestMapping("/api/papeis") // URL base: /api/papeis
@Tag(name = "Papéis", description = "Endpoints para gerenciar papéis de usuário (roles)")
public class PapelController {

    @Autowired
    private EntityManagerFactory emf;

    // --- GET: Listar todos os papéis ---
    @GetMapping
    @Operation(summary = "Listar todos os papéis", description = "Retorna uma lista com todos os papéis cadastrados")
    public List<Papel> listarTodos() throws PersistenciaDawException {
        PapelDAO dao = new PapelDAOImpl(emf);
        return dao.getAll();
    }

    // --- GET: Buscar papel por ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Buscar papel por ID", description = "Retorna os detalhes de um papel específico pelo seu ID")
    public ResponseEntity<Papel> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        PapelDAO dao = new PapelDAOImpl(emf);
        Papel papel = dao.getByID(id);
        if (papel != null) {
            return ResponseEntity.ok(papel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- POST: Salvar novo papel ---
    @PostMapping
    @Operation(summary = "Criar novo papel", description = "Cadastra um novo papel no banco de dados")
    public ResponseEntity<Papel> salvar(@RequestBody Papel papel) throws PersistenciaDawException {
        PapelDAO dao = new PapelDAOImpl(emf);
        try {
            dao.save(papel);
            return ResponseEntity.status(HttpStatus.CREATED).body(papel);
        } catch (PersistenceException e) {
            System.err.println("Erro ao salvar papel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao salvar papel: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- PUT: Atualizar papel existente ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar papel", description = "Atualiza as informações de um papel existente pelo seu ID")
    public ResponseEntity<Papel> atualizar(@PathVariable Long id, @RequestBody Papel papel) throws PersistenciaDawException {
        PapelDAO dao = new PapelDAOImpl(emf);
        Papel papelExistente = dao.getByID(id);

        if (papelExistente != null) {
            papel.setId(id); // Garante que o ID no objeto é o mesmo da URL
            dao.update(papel);
            return ResponseEntity.ok(papel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- DELETE: Remover papel por ID ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover papel", description = "Exclui um papel do banco de dados pelo seu ID")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        PapelDAO dao = new PapelDAOImpl(emf);
        Papel papelExistente = dao.getByID(id);

        if (papelExistente != null) {
            try {
                dao.delete(id);
                return ResponseEntity.noContent().build();
            } catch (PersistenceException e) {
                // Pode ser um erro de chave estrangeira (se o papel estiver associado a usuários)
                System.err.println("Erro ao deletar papel: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}