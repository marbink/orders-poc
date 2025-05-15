package dev.marbink.orders_poc.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "\"order\"")
@NoArgsConstructor
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "order_uuid", updatable = false, nullable = false)
  private UUID orderUuid;

  @Column(name = "customer_uuid", nullable = false)
  private UUID customerUuid;

  @Column(name = "description")
  private String description;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderProduct> orderProducts = new ArrayList<>();
}
