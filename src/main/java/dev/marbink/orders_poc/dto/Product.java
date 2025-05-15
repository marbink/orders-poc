package dev.marbink.orders_poc.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class Product {
  @NotNull UUID productUuid;
  Integer quantity;
}
