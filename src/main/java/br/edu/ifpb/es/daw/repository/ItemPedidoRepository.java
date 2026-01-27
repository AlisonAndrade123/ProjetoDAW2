package br.edu.ifpb.es.daw.repository;

import br.edu.ifpb.es.daw.entities.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long>, JpaSpecificationExecutor<ItemPedido> {}
