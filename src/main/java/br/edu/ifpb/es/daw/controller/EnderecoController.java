package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.EnderecoDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.EnderecoDAOImpl;
import br.edu.ifpb.es.daw.entities.Endereco;
import br.edu.ifpb.es.daw.entities.Usuario; // Precisamos da entidade Usuario
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
@RequestMapping("/api/enderecos") // URL base: /api/enderecos
@Tag(name = "Endereços", description = "Endpoints para gerenciar endereços de usuários")
public class EnderecoController {

    @Autowired
    private EntityManagerFactory emf;

    // --- GET: Listar todos os endereços ---
    @GetMapping
    @Operation(summary = "Listar todos os endereços", description = "Retorna uma lista com todos os endereços cadastrados")
    public List<Endereco> listarTodos() throws PersistenciaDawException {
        EnderecoDAO dao = new EnderecoDAOImpl(emf);
        return dao.getAll();
    }

    // --- GET: Buscar endereço por ID ---
    @GetMapping("/{id}")
    @Operation(summary = "Buscar endereço por ID", description = "Retorna os detalhes de um endereço específico pelo seu ID")
    public ResponseEntity<Endereco> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        EnderecoDAO dao = new EnderecoDAOImpl(emf);
        Endereco endereco = dao.getByID(id);
        if (endereco != null) {
            return ResponseEntity.ok(endereco);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar novo endereço", description = "Cadastra um novo endereço. O usuário associado deve ter um ID válido no JSON.")
    public ResponseEntity<Endereco> salvar(@RequestBody Endereco endereco) throws PersistenciaDawException {
        EnderecoDAO dao = new EnderecoDAOImpl(emf);

        try {
            if (endereco.getUsuario() == null || endereco.getUsuario().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Retorna 400 se não tiver ID de usuário
            }

            Usuario usuarioAssociado = new Usuario();
            usuarioAssociado.setId(endereco.getUsuario().getId());
            endereco.setUsuario(usuarioAssociado);


            dao.save(endereco);
            return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
        } catch (PersistenceException e) {
            System.err.println("Erro ao salvar endereço: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao salvar endereço: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar endereço", description = "Atualiza as informações de um endereço existente pelo seu ID.")
    public ResponseEntity<Endereco> atualizar(@PathVariable Long id, @RequestBody Endereco endereco) throws PersistenciaDawException {
        EnderecoDAO dao = new EnderecoDAOImpl(emf);
        Endereco enderecoExistente = dao.getByID(id);

        if (enderecoExistente != null) {
            endereco.setId(id);

            if (endereco.getUsuario() == null || endereco.getUsuario().getId() == null) {
                endereco.setUsuario(enderecoExistente.getUsuario());
            } else {
                Usuario usuarioAssociado = new Usuario();
                usuarioAssociado.setId(endereco.getUsuario().getId());
                endereco.setUsuario(usuarioAssociado);
            }

            dao.update(endereco);
            return ResponseEntity.ok(endereco);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover endereço", description = "Exclui um endereço do banco de dados pelo seu ID.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        EnderecoDAO dao = new EnderecoDAOImpl(emf);
        Endereco enderecoExistente = dao.getByID(id);

        if (enderecoExistente != null) {
            try {
                dao.delete(id);
                return ResponseEntity.noContent().build();
            } catch (PersistenceException e) {
                System.err.println("Erro ao deletar endereço: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}