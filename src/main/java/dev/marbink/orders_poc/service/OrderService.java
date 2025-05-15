package dev.marbink.orders_poc.service;

import dev.marbink.orders_poc.dto.Order;
import dev.marbink.orders_poc.entity.OrderProduct;
import dev.marbink.orders_poc.entity.Product;
import dev.marbink.orders_poc.exception.NotFoundException;
import dev.marbink.orders_poc.exception.OutOfStockException;
import dev.marbink.orders_poc.mapper.OrderMapper;
import dev.marbink.orders_poc.repository.OrderProductRepository;
import dev.marbink.orders_poc.repository.OrderRepository;
import dev.marbink.orders_poc.repository.OrderRepositoryUtils;
import dev.marbink.orders_poc.repository.ProductRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  public static final String ORDER_NOT_FOUND = "Order not found.";

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;
  private final ProductRepository productRepository;
  private final OrderProductRepository orderProductRepository;

  public List<Order> filterOrders(String query, LocalDate day) {
    return orderRepository
        .findAll(
            OrderRepositoryUtils.notMandatoryDescriptionContainsIgnoreCase(query)
                .and(OrderRepositoryUtils.notMandatoryCreationDate(day)))
        .stream()
        .map(orderMapper::toOrderDto)
        .toList();
  }

  public Order getOrder(@NotNull UUID orderUuid) {
    return orderRepository
        .findById(orderUuid)
        .map(orderMapper::toOrderDto)
        .orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
  }

  @Transactional
  public void deleteOrder(@Valid @NotNull UUID orderUuid) {
    if (!orderRepository.existsById(orderUuid)) {
      throw new NotFoundException(ORDER_NOT_FOUND);
    }
    cleanOrderProducts(orderUuid);
    orderRepository.deleteById(orderUuid);
  }

  @Transactional
  public Order createOrder(@NotNull Order order) {
    if (order.getOrderUuid() != null) {
      throw new IllegalArgumentException();
    }
    return upsertOrder(order);
  }

  @Transactional
  public Order updateOrder(@NotNull Order order) {
    if (!orderRepository.existsById(order.getOrderUuid())) {
      throw new NotFoundException(ORDER_NOT_FOUND);
    }
    return upsertOrder(order);
  }

  // Not setting this private for testing purposes, consider using @VisibleForTesting instead.
  Order upsertOrder(Order order) {
    var orderEntity = orderMapper.toOrder(order);
    cleanOrderProducts(orderEntity.getOrderUuid());

    for (var item : order.getProducts()) {
      Product product =
          productRepository
              .findAndLockByProductUuid(item.getProductUuid())
              .orElseThrow(() -> new NotFoundException("Product not found"));

      if (product.getAvailableQuantity() < item.getQuantity()) {
        throw new OutOfStockException("Not enough stock for product " + product.getProductUuid());
      }

      product.setAvailableQuantity(product.getAvailableQuantity() - item.getQuantity());
      productRepository.save(product);

      OrderProduct orderProduct = new OrderProduct();
      orderProduct.setOrder(orderEntity);
      orderProduct.setProduct(product);
      orderProduct.setQuantity(item.getQuantity());

      orderEntity.getOrderProducts().add(orderProduct);
    }

    orderEntity = orderRepository.save(orderEntity);
    return orderMapper.toOrderDto(orderEntity);
  }

  // Not setting this private for testing purposes, consider using @VisibleForTesting instead.
  void cleanOrderProducts(UUID orderUuid) {
    List<OrderProduct> orderProducts = orderProductRepository.findByOrderOrderUuid(orderUuid);
    orderProducts.forEach(
        orderProduct -> {
          Product product = orderProduct.getProduct();
          product.setAvailableQuantity(product.getAvailableQuantity() + orderProduct.getQuantity());
          productRepository.save(product);
        });
  }
}
