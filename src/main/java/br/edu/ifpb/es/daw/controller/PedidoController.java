// PedidoController.java
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

    @GetMapping
    public List<Pedido> listarTodos() throws PersistenciaDawException {
        PedidoDAO dao = new PedidoDAOImpl(emf);
        return dao.getAll();
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

            // usuario obrigatório
            if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            double total = 0.0;

            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedido item : pedido.getItens()) {
                    // liga bidirecional (cascade funcionar)
                    item.setPedido(pedido);

                    Integer qtd = item.getQuantidade();
                    Double preco = item.getPrecoUnitario();

                    if (qtd == null) qtd = 0;
                    if (preco == null) preco = 0.0;

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

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido pedido) throws PersistenciaDawException {
        PedidoDAO pedidoDAO = new PedidoDAOImpl(emf);
        Pedido existente = pedidoDAO.getByID(id);

        if (existente == null) return ResponseEntity.notFound().build();

        pedido.setId(id);

        // usuario obrigatório
        if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        double total = 0.0;

        if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);

                Integer qtd = item.getQuantidade();
                Double preco = item.getPrecoUnitario();

                if (qtd == null) qtd = 0;
                if (preco == null) preco = 0.0;

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
