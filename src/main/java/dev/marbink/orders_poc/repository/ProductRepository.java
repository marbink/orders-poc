package dev.marbink.orders_poc.repository;

import dev.marbink.orders_poc.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Product> findAndLockByProductUuid(UUID productUuid);
}
