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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private EntityManagerFactory emf;

    // ✅ GET /api/pedidos?page=0&size=10&usuarioId=1&status=ENVIADO&sort=dataDoPedido,desc
    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodos(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) String sort
    ) throws PersistenciaDawException {

        PedidoDAO dao = new PedidoDAOImpl(emf);
        List<Pedido> pedidos = dao.getAll();

        if (usuarioId != null) {
            pedidos = pedidos.stream()
                    .filter(p -> p.getUsuario() != null && usuarioId.equals(p.getUsuario().getId()))
                    .collect(Collectors.toList());
        }

        if (status != null) {
            pedidos = pedidos.stream()
                    .filter(p -> status.equals(p.getStatus()))
                    .collect(Collectors.toList());
        }

        // sort simples: sort=dataDoPedido,desc | sort=valorTotal,asc
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String campo = parts[0].trim();
            String direcao = parts.length > 1 ? parts[1].trim().toLowerCase() : "asc";

            Comparator<Pedido> comp = null;

            if ("dataDoPedido".equalsIgnoreCase(campo)) {
                comp = Comparator.comparing(Pedido::getDataDoPedido, Comparator.nullsLast(LocalDateTime::compareTo));
            } else if ("valorTotal".equalsIgnoreCase(campo)) {
                comp = Comparator.comparing(Pedido::getValorTotal, Comparator.nullsLast(Double::compareTo));
            }

            if (comp != null) {
                if ("desc".equals(direcao)) comp = comp.reversed();
                pedidos = pedidos.stream().sorted(comp).collect(Collectors.toList());
            }
        }

        // paginação
        int from = Math.max(0, page * size);
        int to = Math.min(pedidos.size(), from + size);
        if (from > pedidos.size()) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(pedidos.subList(from, to));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        Pedido pedido = dao.getByID(id);
        return (pedido != null) ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Pedido> salvar(@RequestBody Pedido pedido) throws PersistenciaDawException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);

        try {
            if (pedido.getDataDoPedido() == null) pedido.setDataDoPedido(LocalDateTime.now());
            if (pedido.getStatus() == null) pedido.setStatus(StatusPedido.ENVIADO);

            if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            double total = 0.0;

            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedido item : pedido.getItens()) {
                    item.setPedido(pedido);
                    Integer qtd = item.getQuantidade() == null ? 0 : item.getQuantidade();
                    Double preco = item.getPrecoUnitario() == null ? 0.0 : item.getPrecoUnitario();
                    total += qtd * preco;
                }
            }

            pedido.setValorTotal(total);

            pedidoDAO.save(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);

        } catch (PersistenceException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ⚠️ se quiser REMOVER atualização, só apagar esse método.
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido pedido) throws PersistenciaDawException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
        Pedido existente = pedidoDAO.getByID(id);

        if (existente == null) return ResponseEntity.notFound().build();

        pedido.setId(id);

        if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        double total = 0.0;

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
                Integer qtd = item.getQuantidade() == null ? 0 : item.getQuantidade();
                Double preco = item.getPrecoUnitario() == null ? 0.0 : item.getPrecoUnitario();
                total += qtd * preco;
            }
        }

        pedido.setValorTotal(total);

        pedidoDAO.update(pedido);
        return ResponseEntity.ok(pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        Pedido pedidoExistente = dao.getByID(id);

        if (pedidoExistente == null) return ResponseEntity.notFound().build();

        try {
            dao.delete(id);
            return ResponseEntity.noContent().build();
        } catch (PersistenceException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
