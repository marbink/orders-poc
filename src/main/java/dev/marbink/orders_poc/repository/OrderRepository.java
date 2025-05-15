package dev.marbink.orders_poc.repository;

import dev.marbink.orders_poc.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository
    extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
  // No custom method needed.
}
