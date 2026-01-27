package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dao.PedidoDAO;
import br.edu.ifpb.es.daw.dao.PersistenciaDawException;
import br.edu.ifpb.es.daw.dao.impl.PedidoDAOImpl;
import br.edu.ifpb.es.daw.entities.ItemPedido;
import br.edu.ifpb.es.daw.entities.Pedido;
import br.edu.ifpb.es.daw.entities.StatusPedido;
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
public class PedidoController {

    @Autowired
    private EntityManagerFactory emf;

    // ================== GET NORMAL ==================
    @GetMapping
    public List<Pedido> listarTodos() throws PersistenciaDawException {
        return new PedidoDAOImpl(emf).getAll();
    }

    // ================== GET PAGINAÇÃO ==================
    @GetMapping("/page")
    public ResponseEntity<List<Pedido>> listarComPaginacao(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws PersistenciaDawException {

        PedidoDAO dao = new PedidoDAOImpl(emf);
        List<Pedido> todos = dao.getAll();

        int start = page * size;
        int end = Math.min(start + size, todos.size());

        if (start >= todos.size()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(todos.subList(start, end));
    }

    // ================== GET FILTRO ==================
    @GetMapping("/filtro")
    public ResponseEntity<List<Pedido>> filtrar(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) Long usuarioId
    ) throws PersistenciaDawException {

        PedidoDAO dao = new PedidoDAOImpl(emf);
        List<Pedido> todos = dao.getAll();

        List<Pedido> filtrados = todos.stream()
                .filter(p ->
                        (status == null || p.getStatus() == status) &&
                                (usuarioId == null || p.getUsuario().getId().equals(usuarioId))
                )
                .toList();

        return ResponseEntity.ok(filtrados);
    }

    // ================== GET POR ID ==================
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        Pedido pedido = new PedidoDAOImpl(emf).getByID(id);
        return (pedido != null) ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    // ================== POST ==================
    @PostMapping
    public ResponseEntity<Pedido> salvar(@RequestBody Pedido pedido) throws PersistenciaDawException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);

        try {
            if (pedido.getDataDoPedido() == null)
                pedido.setDataDoPedido(LocalDateTime.now());

            if (pedido.getStatus() == null)
                pedido.setStatus(StatusPedido.ENVIADO);

            if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null)
                return ResponseEntity.badRequest().build();

            double total = 0.0;

            if (pedido.getItens() != null) {
                for (ItemPedido item : pedido.getItens()) {
                    item.setPedido(pedido);
                    Integer qtd = item.getQuantidade() != null ? item.getQuantidade() : 0;
                    Double preco = item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0;
                    total += qtd * preco;
                }
            }

            pedido.setValorTotal(total);
            pedidoDAO.save(pedido);

            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ================== PATCH STATUS ==================
    @PatchMapping("/{id}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long id,
            @RequestBody StatusPedido status)
            throws PersistenciaDawException {

        PedidoDAO dao = new PedidoDAOImpl(emf);
        Pedido pedido = dao.getByID(id);

        if (pedido == null)
            return ResponseEntity.notFound().build();

        pedido.setStatus(status);
        dao.update(pedido);

        return ResponseEntity.ok(pedido);
    }

    // ================== DELETE ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        Pedido pedido = dao.getByID(id);

        if (pedido == null)
            return ResponseEntity.notFound().build();

        dao.delete(id);
        return ResponseEntity.noContent().build();
    }
}
