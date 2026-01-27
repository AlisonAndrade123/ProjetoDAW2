package br.edu.ifpb.es.daw.specs;

import br.edu.ifpb.es.daw.entities.Pedido;
import br.edu.ifpb.es.daw.entities.StatusPedido;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PedidoSpecs {
    public static Specification<Pedido> usuarioId(Long usuarioId) {
        return (root, q, cb) -> usuarioId == null ? null :
                cb.equal(root.get("usuario").get("id"), usuarioId);
    }

    public static Specification<Pedido> status(StatusPedido status) {
        return (root, q, cb) -> status == null ? null :
                cb.equal(root.get("status"), status);
    }

    public static Specification<Pedido> dataEntre(LocalDateTime ini, LocalDateTime fim) {
        return (root, q, cb) -> {
            if (ini == null && fim == null) return null;
            if (ini == null) return cb.lessThanOrEqualTo(root.get("dataDoPedido"), fim);
            if (fim == null) return cb.greaterThanOrEqualTo(root.get("dataDoPedido"), ini);
            return cb.between(root.get("dataDoPedido"), ini, fim);
        };
    }
}
