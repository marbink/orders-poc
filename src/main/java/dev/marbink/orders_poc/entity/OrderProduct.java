package dev.marbink.orders_poc.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "order_product")
@NoArgsConstructor
public class OrderProduct extends BaseEntity {

  @EmbeddedId private OrderProductKey orderProductId = new OrderProductKey();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("orderUuid")
  @JoinColumn(name = "order_uuid")
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("productUuid")
  @JoinColumn(name = "product_uuid")
  private Product product;

  @Column(name = "quantity", nullable = false)
  private int quantity;

  public void setProduct(Product product) {
    this.product = product;
    if (product != null) {
      this.orderProductId.setProductUuid(product.getProductUuid());
    }
  }

  public void setOrder(Order order) {
    this.order = order;
    if (order != null) {
      this.orderProductId.setOrderUuid(order.getOrderUuid());
    }
  }

  @Getter
  @Setter
  @Embeddable
  @NoArgsConstructor
  public static class OrderProductKey {
    private UUID orderUuid;
    private UUID productUuid;
  }
}
