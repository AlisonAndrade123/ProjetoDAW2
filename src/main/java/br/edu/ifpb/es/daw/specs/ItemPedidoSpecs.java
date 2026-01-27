package br.edu.ifpb.es.daw.specs;

import br.edu.ifpb.es.daw.entities.ItemPedido;
import org.springframework.data.jpa.domain.Specification;

public class ItemPedidoSpecs {
    public static Specification<ItemPedido> pedidoId(Long pedidoId) {
        return (root, q, cb) -> pedidoId == null ? null :
                cb.equal(root.get("pedido").get("id"), pedidoId);
    }

    public static Specification<ItemPedido> produtoId(Long produtoId) {
        return (root, q, cb) -> produtoId == null ? null :
                cb.equal(root.get("produto").get("id"), produtoId);
    }
}
