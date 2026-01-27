package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.*;
import br.edu.ifpb.es.daw.dao.impl.*;
import br.edu.ifpb.es.daw.dto.ItemPedidoDTO;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import br.edu.ifpb.es.daw.entities.Pedido;
import br.edu.ifpb.es.daw.entities.Produto;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
public class ItemPedidoController {

    @Autowired
    private EntityManagerFactory emf;

    // ================== GET NORMAL ==================
    @GetMapping
    public List<ItemPedido> listarTodos() throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        return dao.getAll();
    }

    // ================== GET PAGINAÇÃO ==================
    @GetMapping("/page")
    public ResponseEntity<List<ItemPedido>> listarComPaginacao(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws PersistenciaDawException {

        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        List<ItemPedido> todos = dao.getAll();

        int start = page * size;
        int end = Math.min(start + size, todos.size());

        if (start >= todos.size()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(todos.subList(start, end));
    }

    // ================== GET FILTRO ==================
    @GetMapping("/filtro")
    public ResponseEntity<List<ItemPedido>> filtrar(
            @RequestParam(required = false) Long pedidoId,
            @RequestParam(required = false) Long produtoId
    ) throws PersistenciaDawException {

        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        List<ItemPedido> todos = dao.getAll();

        List<ItemPedido> filtrados = todos.stream()
                .filter(i ->
                        (pedidoId == null || i.getPedido().getId().equals(pedidoId)) &&
                                (produtoId == null || i.getProduto().getId().equals(produtoId))
                )
                .toList();

        return ResponseEntity.ok(filtrados);
    }

    // ================== GET POR ID ==================
    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        ItemPedido item = dao.getByID(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // ================== POST ==================
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody ItemPedidoDTO dto) {
        try {
            ItemPedidoDAO itemDAO = new ItemPedidoDAOImpl(emf);
            PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
            ProdutoDAO produtoDAO = new ProdutoDAOImpl(emf);

            if (dto.getPedidoId() == null || dto.getProdutoId() == null) {
                return ResponseEntity.badRequest().body("pedidoId e produtoId são obrigatórios");
            }

            Pedido pedido = pedidoDAO.getByID(dto.getPedidoId());
            Produto produto = produtoDAO.getByID(dto.getProdutoId());

            if (pedido == null || produto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido ou Produto não encontrado");
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(dto.getQuantidade());
            item.setPrecoUnitario(dto.getPrecoUnitario());

            itemDAO.save(item);

            return ResponseEntity.status(HttpStatus.CREATED).body(item);

        } catch (PersistenciaDawException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro ao salvar ItemPedido");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno");
        }
    }

    // ================== DELETE ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        ItemPedido item = dao.getByID(id);

        if (item == null) return ResponseEntity.notFound().build();

        try {
            dao.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
