package br.edu.ifpb.es.daw.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataDoPedido;

    private String status; // Ex: AGUARDANDO_PAGAMENTO, ENVIADO, ENTREGUE

    @Column(nullable = false)
    private Double valorTotal;


    public Pedido() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataDoPedido() { return dataDoPedido; }
    public void setDataDoPedido(LocalDateTime dataDoPedido) { this.dataDoPedido = dataDoPedido; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pedido pedido = (Pedido) o;
        return Objects.equals(id, pedido.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", dataDoPedido=" + dataDoPedido +
                ", status='" + status + '\'' +
                ", valorTotal=" + valorTotal +
                '}';
    }
}