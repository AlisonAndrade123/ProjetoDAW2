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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itens-pedido")
public class ItemPedidoController {

    @Autowired
    private EntityManagerFactory emf;

    // ✅ GET /api/itens-pedido?page=0&size=10&pedidoId=1&produtoId=4
    @GetMapping
    public ResponseEntity<List<ItemPedido>> listarTodos(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) Long pedidoId,
            @RequestParam(required = false) Long produtoId
    ) throws PersistenciaDawException {

        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        List<ItemPedido> itens = dao.getAll();

        if (pedidoId != null) {
            itens = itens.stream()
                    .filter(i -> i.getPedido() != null && pedidoId.equals(i.getPedido().getId()))
                    .collect(Collectors.toList());
        }

        if (produtoId != null) {
            itens = itens.stream()
                    .filter(i -> i.getProduto() != null && produtoId.equals(i.getProduto().getId()))
                    .collect(Collectors.toList());
        }

        // paginação
        int from = Math.max(0, page * size);
        int to = Math.min(itens.size(), from + size);
        if (from > itens.size()) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(itens.subList(from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        ItemPedidoDAO dao = new ItemPedidoDAOImpl(emf);
        ItemPedido item = dao.getByID(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    // ✅ POST /api/itens-pedido
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido ou Produto não encontrado");
            }

            if (dto.getQuantidade() == null || dto.getQuantidade() <= 0) {
                return ResponseEntity.badRequest().body("quantidade deve ser > 0");
            }

            Double precoUnit = dto.getPrecoUnitario();
            if (precoUnit == null) {
                // opcional: se não vier, pega do produto
                precoUnit = produto.getPreco();
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(dto.getQuantidade());
            item.setPrecoUnitario(precoUnit);

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
