package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.UsuarioDAO;
import br.edu.ifpb.es.daw.dao.impl.UsuarioDAOImpl;
import br.edu.ifpb.es.daw.entities.Usuario;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuários", description = "Endpoints para gerenciar usuários")
public class UsuarioController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados")
    public List<Usuario> listarTodos() throws PersistenciaDawException {
        UsuarioDAO dao = new UsuarioDAOImpl(emf);
        return dao.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico pelo seu ID")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        UsuarioDAO dao = new UsuarioDAOImpl(emf);
        Usuario usuario = dao.getByID(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cadastra um novo usuário no banco de dados. " +
            "Se incluir Endereços no JSON, eles serão salvos em cascata. " +
            "Se incluir Papéis com ID no JSON, eles devem existir no banco e serão associados (MERGE).")
    public ResponseEntity<Usuario> salvar(@RequestBody Usuario usuario) throws PersistenciaDawException {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);

        try {
            if (usuario.getEnderecos() != null) {
                usuario.getEnderecos().forEach(endereco -> endereco.setUsuario(usuario));
            }

            usuarioDAO.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (PersistenceException e) {
            System.err.println("Erro de persistência ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao salvar usuário: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário existente pelo seu ID.")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuario) throws PersistenciaDawException {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);
        Usuario usuarioExistente = usuarioDAO.getByID(id);

        if (usuarioExistente != null) {
            usuario.setId(id); // Garante que o ID no objeto é o mesmo da URL

            if (usuario.getEnderecos() != null) {
                usuario.getEnderecos().forEach(endereco -> endereco.setUsuario(usuario));
            }

            usuarioDAO.update(usuario);
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover usuário", description = "Exclui um usuário do banco de dados pelo seu ID.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        UsuarioDAO usuarioDAO = new UsuarioDAOImpl(emf);
        Usuario usuarioExistente = usuarioDAO.getByID(id);

        if (usuarioExistente != null) {
            try {
                usuarioDAO.delete(id);
                return ResponseEntity.noContent().build();
            } catch (PersistenceException e) {
                System.err.println("Erro ao deletar usuário: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}