package dev.marbink.orders_poc.service;

import static com.flextrade.jfixture.FixtureAnnotations.initFixtures;
import static dev.marbink.orders_poc.service.OrderService.ORDER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import dev.marbink.orders_poc.entity.Order;
import dev.marbink.orders_poc.entity.OrderProduct;
import dev.marbink.orders_poc.entity.Product;
import dev.marbink.orders_poc.exception.NotFoundException;
import dev.marbink.orders_poc.exception.OutOfStockException;
import dev.marbink.orders_poc.mapper.OrderMapper;
import dev.marbink.orders_poc.repository.OrderProductRepository;
import dev.marbink.orders_poc.repository.OrderRepository;
import dev.marbink.orders_poc.repository.ProductRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @InjectMocks OrderService orderService;
  @Mock OrderRepository orderRepository;
  @Mock OrderMapper orderMapper;
  @Mock ProductRepository productRepository;
  @Mock OrderProductRepository orderProductRepository;
  @Fixture Order orderEntity;
  @Fixture Product productEntity;
  @Fixture OrderProduct orderProductEntity;
  @Fixture dev.marbink.orders_poc.dto.Order orderDto;

  @BeforeEach
  void setUp() {
    JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    initFixtures(this, jFixture);

    // Need to manually handle circular dependencies
    orderEntity
        .getOrderProducts()
        .forEach(
            orderProduct -> {
              orderProduct.setOrder(orderEntity);
              orderProduct.setProduct(productEntity);
            });
  }

  @Test
  void filterOrders() {
    when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(orderEntity));
    when(orderMapper.toOrderDto(orderEntity)).thenReturn(orderDto);

    var result = orderService.filterOrders(null, LocalDate.now());

    assertEquals(1, result.size());
    assertEquals(orderDto, result.get(0));

    verify(orderRepository).findAll(any(Specification.class));
    verify(orderMapper).toOrderDto(orderEntity);
  }

  @Test
  void getOrder_OrderFound() {
    when(orderRepository.findById(orderEntity.getOrderUuid())).thenReturn(Optional.of(orderEntity));
    when(orderMapper.toOrderDto(orderEntity)).thenReturn(orderDto);

    var result = orderService.getOrder(orderEntity.getOrderUuid());

    assertEquals(orderDto, result);
    verify(orderRepository).findById(orderEntity.getOrderUuid());
    verify(orderMapper).toOrderDto(orderEntity);
  }

  @Test
  void getOrder_OrderNotFound() {
    when(orderRepository.findById(orderEntity.getOrderUuid())).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> orderService.getOrder(orderEntity.getOrderUuid()));
    assertEquals(ORDER_NOT_FOUND, exception.getMessage());
    verify(orderRepository).findById(orderEntity.getOrderUuid());
    verify(orderMapper, never()).toOrderDto(any());
  }

  @Test
  void deleteOrder_OrderFound() {
    when(orderRepository.existsById(orderEntity.getOrderUuid())).thenReturn(true);
    when(orderProductRepository.findByOrderOrderUuid(orderEntity.getOrderUuid()))
        .thenReturn(List.of(orderProductEntity));

    orderService.deleteOrder(orderEntity.getOrderUuid());

    verify(orderRepository).existsById(orderEntity.getOrderUuid());
    verify(orderRepository).deleteById(orderEntity.getOrderUuid());
    verify(orderProductRepository).findByOrderOrderUuid(orderEntity.getOrderUuid());
    verify(productRepository).save(orderProductEntity.getProduct());
  }

  @Test
  void deleteOrder_OrderNotFound() {
    when(orderRepository.existsById(orderEntity.getOrderUuid())).thenReturn(false);

    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> orderService.deleteOrder(orderEntity.getOrderUuid()));
    assertEquals(ORDER_NOT_FOUND, exception.getMessage());
    verify(orderRepository).existsById(orderEntity.getOrderUuid());
    verify(orderRepository, never()).deleteById(any());
  }

  @Test
  void createOrder_HappyPath() {
    when(orderMapper.toOrder(orderDto)).thenReturn(orderEntity);
    List<OrderProduct> orderProductList = List.of(orderProductEntity);
    when(orderProductRepository.findByOrderOrderUuid(orderEntity.getOrderUuid()))
        .thenReturn(orderProductList);
    when(productRepository.findAndLockByProductUuid(any())).thenReturn(Optional.of(productEntity));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(orderMapper.toOrderDto(orderEntity)).thenReturn(orderDto);
    orderDto.setOrderUuid(null);
    productEntity.setAvailableQuantity(Integer.MAX_VALUE);

    var result = orderService.createOrder(orderDto);

    assertThat(result).usingRecursiveComparison().ignoringFields("orderUuid").isEqualTo(orderDto);
    verify(orderMapper).toOrder(orderDto);
    verify(orderProductRepository).findByOrderOrderUuid(orderEntity.getOrderUuid());
    verify(productRepository, times(orderDto.getProducts().size())).findAndLockByProductUuid(any());
    verify(productRepository, times(orderProductList.size() + orderDto.getProducts().size()))
        .save(any());
    verify(orderRepository).save(any());
    verify(orderMapper).toOrderDto(orderEntity);
  }

  @Test
  void createOrder_OrderUuidNotNull() {
    orderDto.setOrderUuid(UUID.randomUUID());

    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderDto));
    verify(orderRepository, never()).save(any());
    verify(orderMapper, never()).toOrder(any());
    verify(orderMapper, never()).toOrderDto(any());
    verify(orderProductRepository, never()).findByOrderOrderUuid(any());
    verify(productRepository, never()).save(any());
  }

  @Test
  void updateOrder_OrderFound() {
    when(orderRepository.existsById(orderDto.getOrderUuid())).thenReturn(true);
    when(orderMapper.toOrder(orderDto)).thenReturn(orderEntity);
    List<OrderProduct> orderProductList = List.of(orderProductEntity);
    when(orderProductRepository.findByOrderOrderUuid(orderEntity.getOrderUuid()))
        .thenReturn(orderProductList);
    when(productRepository.findAndLockByProductUuid(any())).thenReturn(Optional.of(productEntity));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(orderMapper.toOrderDto(orderEntity)).thenReturn(orderDto);
    productEntity.setAvailableQuantity(Integer.MAX_VALUE);

    var result = orderService.updateOrder(orderDto);

    assertEquals(orderDto, result);
    verify(orderMapper).toOrder(orderDto);
    verify(orderProductRepository).findByOrderOrderUuid(orderEntity.getOrderUuid());
    verify(productRepository, times(orderDto.getProducts().size())).findAndLockByProductUuid(any());
    verify(productRepository, times(orderProductList.size() + orderDto.getProducts().size()))
        .save(any());
    verify(orderRepository).save(any());
    verify(orderMapper).toOrderDto(orderEntity);
  }

  @Test
  void updateOrder_OrderNotFound() {
    when(orderRepository.existsById(orderDto.getOrderUuid())).thenReturn(false);

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> orderService.updateOrder(orderDto));
    assertEquals(ORDER_NOT_FOUND, exception.getMessage());
    verify(orderRepository).existsById(orderDto.getOrderUuid());
    verify(orderRepository, never()).save(any());
    verify(orderMapper, never()).toOrder(any());
    verify(orderMapper, never()).toOrderDto(any());
  }

  @Test
  void upsertOrder_HappyPath() {
    when(orderMapper.toOrder(orderDto)).thenReturn(orderEntity);
    List<OrderProduct> orderProductList = List.of(orderProductEntity);
    when(orderProductRepository.findByOrderOrderUuid(orderEntity.getOrderUuid()))
        .thenReturn(orderProductList);
    when(productRepository.findAndLockByProductUuid(any())).thenReturn(Optional.of(productEntity));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(orderMapper.toOrderDto(orderEntity)).thenReturn(orderDto);
    productEntity.setAvailableQuantity(Integer.MAX_VALUE);

    var result = orderService.upsertOrder(orderDto);

    assertThat(result).usingRecursiveComparison().ignoringFields("orderUuid").isEqualTo(orderDto);
    verify(orderMapper).toOrder(orderDto);
    verify(orderProductRepository).findByOrderOrderUuid(orderEntity.getOrderUuid());
    verify(productRepository, times(orderDto.getProducts().size())).findAndLockByProductUuid(any());
    verify(productRepository, times(orderProductList.size() + orderDto.getProducts().size()))
        .save(any());
    verify(orderRepository).save(any());
    verify(orderMapper).toOrderDto(orderEntity);
  }

  @Test
  void upsertOrder_ProductNotFound() {
    when(orderMapper.toOrder(orderDto)).thenReturn(orderEntity);
    when(orderProductRepository.findByOrderOrderUuid(orderEntity.getOrderUuid()))
        .thenReturn(Collections.emptyList());
    when(productRepository.findAndLockByProductUuid(any())).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> orderService.upsertOrder(orderDto));
    assertEquals("Product not found", exception.getMessage());
    verify(orderMapper).toOrder(orderDto);
    verify(orderProductRepository).findByOrderOrderUuid(orderEntity.getOrderUuid());
    verify(productRepository).findAndLockByProductUuid(any());
    verify(productRepository, never()).save(any());
    verify(orderRepository, never()).save(any());
    verify(orderMapper, never()).toOrderDto(any());
  }

  @Test
  void upsertOrder_InsufficientStock() {
    when(orderMapper.toOrder(orderDto)).thenReturn(orderEntity);
    when(orderProductRepository.findByOrderOrderUuid(orderEntity.getOrderUuid()))
        .thenReturn(Collections.emptyList());
    when(productRepository.findAndLockByProductUuid(any())).thenReturn(Optional.of(productEntity));
    productEntity.setAvailableQuantity(0);

    OutOfStockException exception =
        assertThrows(OutOfStockException.class, () -> orderService.upsertOrder(orderDto));
    assertEquals(
        "Not enough stock for product " + productEntity.getProductUuid(), exception.getMessage());
    verify(orderMapper).toOrder(orderDto);
    verify(orderProductRepository).findByOrderOrderUuid(orderEntity.getOrderUuid());
    verify(productRepository).findAndLockByProductUuid(any());
    verify(productRepository, never()).save(any());
    verify(orderRepository, never()).save(any());
    verify(orderMapper, never()).toOrderDto(any());
  }
}
