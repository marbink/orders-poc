package dev.marbink.orders_poc.repository;

import dev.marbink.orders_poc.entity.OrderProduct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository
    extends JpaRepository<OrderProduct, OrderProduct.OrderProductKey> {

  List<OrderProduct> findByOrderOrderUuid(@Valid @NotNull UUID orderUuid);
}
