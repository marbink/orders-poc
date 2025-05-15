package dev.marbink.orders_poc.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class Order {
  UUID orderUuid;
  @NotNull String customerUuid;
  String description;
  @NotEmpty List<Product> products;
}
