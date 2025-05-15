package dev.marbink.orders_poc.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Setter
@Getter
@Table(name = "product")
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "product_uuid")
  private UUID productUuid;

  @Column(name = "product_name")
  private String productName;

  @Column(name = "price")
  private BigDecimal price;

  @Column(name = "available_quantity")
  private int availableQuantity;

  @OneToMany(mappedBy = "product")
  private Set<OrderProduct> orderProducts;
}
