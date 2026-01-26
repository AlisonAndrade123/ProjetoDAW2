package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.ItemPedidoDAO;
import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.ProdutoDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.ItemPedidoDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.dao.impl.ProdutoDAOImpl;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import br.edu.ifpb.es.daw.entities.Pedido;
import br.edu.ifpb.es.daw.entities.Produto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException; // Este import pode ser mantido ou removido se não for usado para outros fins
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
@Tag(name = "Itens de Pedido", description = "Endpoints para gerenciar itens de pedidos")
public class ItemPedidoController {

    @Autowired
    private EntityManagerFactory emf;

    @GetMapping
    @Operation(summary = "Listar todos os itens de pedido", description = "Retorna uma lista com todos os itens de pedido cadastrados")
    public List<ItemPedido> listarTodos() throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        return dao.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item de pedido por ID", description = "Retorna os detalhes de um item de pedido específico pelo seu ID")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        ItemPedido item = dao.getByID(id);
        if (item != null) {
            return ResponseEntity.ok(item);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- POST: Salvar novo item de pedido (VERSÃO CORRIGIDA E FINAL) ---
    @PostMapping
    @Operation(summary = "Criar novo item de pedido", description = "Cadastra um novo item de pedido. Requer IDs de Pedido e Produto válidos.")
    public ResponseEntity<ItemPedido> salvar(@RequestBody ItemPedido itemPedido) throws PersistenciaDawException {
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(emf);
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);   // Para buscar o Pedido real
        ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf); // Para buscar o Produto real

        try {
            // 1. Validar e associar o Pedido REAL
            if (itemPedido.getPedido() == null || itemPedido.getPedido().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Pedido pedidoReal = pedidoDAO.getByID(itemPedido.getPedido().getId());
            if (pedidoReal == null) {
                throw new RuntimeException("Pedido com ID " + itemPedido.getPedido().getId() + " não encontrado.");
            }
            itemPedido.setPedido(pedidoReal); // Associa o pedido real

            // 2. Validar e associar o Produto REAL
            if (itemPedido.getProduto() == null || itemPedido.getProduto().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            Produto produtoReal = produtoDAO.getByID(itemPedido.getProduto().getId());
            if (produtoReal == null) {
                throw new RuntimeException("Produto com ID " + itemPedido.getProduto().getId() + " não encontrado.");
            }
            itemPedido.setProduto(produtoReal); // Associa o produto real

            // 3. Salvar o ItemPedido
            itemPedidoDAO.save(itemPedido);

            // Lógica opcional para atualizar o valor total do pedido.
            // Se quiséssemos atualizar o valor total do Pedido pai, teríamos que buscar o PedidoReal no EntityManager
            // e recalcular o total. Para simplicidade, não faremos isso aqui, mas em um sistema real seria feito.
            // pedidoReal.getItens().add(itemPedido); // Adiciona na lista gerenciada
            // pedidoReal.setValorTotal(pedidoReal.getValorTotal() + (itemPedido.getQuantidade() * itemPedido.getPrecoUnitario()));
            // pedidoDAO.update(pedidoReal); // Salva o pedido pai atualizado

            return ResponseEntity.status(HttpStatus.CREATED).body(itemPedido);
        } catch (RuntimeException e) { // Capturamos a RuntimeException lançada pela validação
            System.err.println("Erro ao salvar item de pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (PersistenciaDawException e) { // Capturamos a exceção do DAO
            System.err.println("Erro de persistência ao salvar item de pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Pode ser FK ou outro problema
        } catch (Exception e) { // Para qualquer outra exceção inesperada
            System.err.println("Erro inesperado ao salvar item de pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- PUT: Atualizar item de pedido existente ---
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item de pedido", description = "Atualiza as informações de um item de pedido existente pelo seu ID.")
    public ResponseEntity<ItemPedido> atualizar(@PathVariable Long id, @RequestBody ItemPedido itemPedido) throws PersistenciaDawException {
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(emf);
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
        ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);

        ItemPedido itemExistente = itemPedidoDAO.getByID(id);

        if (itemExistente != null) {
            itemPedido.setId(id);

            // Re-associar Pedido REAL
            if (itemPedido.getPedido() == null || itemPedido.getPedido().getId() == null) {
                itemPedido.setPedido(itemExistente.getPedido()); // Mantém o pedido existente
            } else {
                Pedido pedidoReal = pedidoDAO.getByID(itemPedido.getPedido().getId());
                if (pedidoReal == null) {
                    throw new RuntimeException("Pedido com ID " + itemPedido.getPedido().getId() + " não encontrado.");
                }
                itemPedido.setPedido(pedidoReal);
            }

            // Re-associar Produto REAL
            if (itemPedido.getProduto() == null || itemPedido.getProduto().getId() == null) {
                itemPedido.setProduto(itemExistente.getProduto()); // Mantém o produto existente
            } else {
                Produto produtoReal = produtoDAO.getByID(itemPedido.getProduto().getId());
                if (produtoReal == null) {
                    throw new RuntimeException("Produto com ID " + itemPedido.getProduto().getId() + " não encontrado.");
                }
                itemPedido.setProduto(produtoReal);
            }

            itemPedidoDAO.update(itemPedido);
            return ResponseEntity.ok(itemPedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- DELETE: Remover item de pedido por ID ---
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover item de pedido", description = "Exclui um item de pedido do banco de dados pelo seu ID.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        ItemPedido itemExistente = dao.getByID(id);

        if (itemExistente != null) {
            try {
                dao.delete(id);
                return ResponseEntity.noContent().build();
            } catch (PersistenceException e) {
                System.err.println("Erro ao deletar item de pedido: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}