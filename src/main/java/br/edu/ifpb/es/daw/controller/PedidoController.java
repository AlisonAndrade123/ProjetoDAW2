package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import br.edu.ifpb.es.daw.entities.Pedido;
import br.edu.ifpb.es.daw.entities.StatusPedido; // Import do seu Enum
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para gerenciar pedidos e seus itens")
public class PedidoController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    @Operation(summary = "Listar todos os pedidos", description = "Retorna uma lista com todos os pedidos cadastrados")
    public List<Pedido> listarTodos() throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        return dao.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido específico pelo seu ID")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        Pedido pedido = dao.getByID(id);
        if (pedido != null) {
            return ResponseEntity.ok(pedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- POST: Salvar novo pedido (VERSÃO SIMPLIFICADA E ESTÁVEL) ---
    @PostMapping
    @Operation(summary = "Criar novo pedido", description = "Cadastra um novo pedido no banco de dados. " +
            "O JSON de entrada DEVE conter os IDs de Usuário e Produtos nos Itens já válidos no banco. " +
            "O valorTotal deve ser calculado pelo cliente ou será 0.0.")
    public ResponseEntity<Pedido> salvar(@RequestBody Pedido pedido) throws PersistenciaDawException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);

        try {
            // Lógica mínima para o JPA: definir data e status padrão se não vierem
            if (pedido.getDataDoPedido() == null) {
                pedido.setDataDoPedido(LocalDateTime.now());
            }
            if (pedido.getStatus() == null) {
                pedido.setStatus(StatusPedido.ENVIADO); // Usando uma constante válida
            }
            // Importante: O valorTotal deve ser enviado no JSON, ou será 0.0

            // Ligar cada item de volta ao pedido (essencial para o Cascade funcionar)
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                pedido.getItens().forEach(item -> {
                    item.setPedido(pedido); // Ligação bidirecional
                });
            }

            // O DAO.save() fará o trabalho de persistir o pedido e seus itens (graças ao CascadeType.ALL)
            // e associar o usuário e produtos (graças ao CascadeType.MERGE ou IDs preenchidos).
            pedidoDAO.save(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (PersistenceException e) {
            System.err.println("Erro de persistência ao salvar pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.err.println("Erro inesperado ao salvar pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- PUT: Atualizar pedido existente (VERSÃO SIMPLIFICADA E ESTÁVEL) ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Atualiza as informações de um pedido existente pelo seu ID. " +
            "O JSON de entrada DEVE conter os IDs de Usuário e Produtos nos Itens já válidos no banco.")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido pedido) throws PersistenciaDawException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
        Pedido pedidoExistente = pedidoDAO.getByID(id); // Buscamos o pedido existente

        if (pedidoExistente != null) {
            pedido.setId(id); // Garante que o ID no objeto é o mesmo da URL

            // Lógica de ligação bidirecional
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                pedido.getItens().forEach(item -> {
                    item.setPedido(pedido);
                });
            }

            // O DAO.update() fará o merge, que é o que precisamos.
            pedidoDAO.update(pedido);
            return ResponseEntity.ok(pedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- DELETE: Remover pedido por ID (Sem mudanças) ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover pedido", description = "Exclui um pedido do banco de dados pelo seu ID.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        Pedido pedidoExistente = dao.getByID(id);

        if (pedidoExistente != null) {
            try {
                dao.delete(id);
                return ResponseEntity.noContent().build();
            } catch (PersistenceException e) {
                System.err.println("Erro ao deletar pedido: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}