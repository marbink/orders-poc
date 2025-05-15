package dev.marbink.orders_poc.controller;

import static com.flextrade.jfixture.FixtureAnnotations.initFixtures;
import static dev.marbink.orders_poc.service.OrderService.ORDER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import dev.marbink.orders_poc.dto.GetOrdersResponse;
import dev.marbink.orders_poc.dto.Order;
import dev.marbink.orders_poc.exception.NotFoundException;
import dev.marbink.orders_poc.service.OrderService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

  @InjectMocks OrderController orderController;
  @Mock OrderService orderService;
  @Fixture Order orderDto;
  @Fixture UUID updateOrderUuid;

  @BeforeEach
  void setUp() {
    JFixture jFixture = new JFixture();
    jFixture.customise().circularDependencyBehaviour().omitSpecimen();
    initFixtures(this, jFixture);
  }

  @Test
  void getOrders() {
    when(orderService.filterOrders(null, null)).thenReturn((List.of(orderDto)));

    GetOrdersResponse result = orderController.getOrders(null, null);

    assertEquals(1, result.getItems().size());
    assertEquals(orderDto, result.getItems().get(0));
    verify(orderService).filterOrders(any(), any());
  }

  @Test
  void createOrder() {
    when(orderService.createOrder(orderDto)).thenReturn(orderDto);

    Order result = orderController.createOrder(orderDto);

    assertEquals(orderDto, result);
    assertNull(result.getOrderUuid());
    verify(orderService).createOrder(orderDto);
  }

  @Test
  void getOrder_OrderFound() {
    when(orderService.getOrder(orderDto.getOrderUuid())).thenReturn(orderDto);

    Order result = orderController.getOrder(orderDto.getOrderUuid());

    assertEquals(orderDto, result);
    verify(orderService).getOrder(orderDto.getOrderUuid());
  }

  @Test
  void getOrder_OrderNotFound() {
    when(orderService.getOrder(orderDto.getOrderUuid()))
        .thenThrow(new NotFoundException(ORDER_NOT_FOUND));

    NotFoundException responseStatusException =
        assertThrows(
            NotFoundException.class, () -> orderController.getOrder(orderDto.getOrderUuid()));
    assertEquals(ORDER_NOT_FOUND, responseStatusException.getMessage());
    verify(orderService).getOrder(orderDto.getOrderUuid());
  }

  @Test
  void deleteOrder_OrderFound() {
    doNothing().when(orderService).deleteOrder(orderDto.getOrderUuid());

    assertDoesNotThrow(() -> orderController.deleteOrder(orderDto.getOrderUuid()));
    verify(orderService).deleteOrder(orderDto.getOrderUuid());
  }

  @Test
  void deleteOrder_OrderNotFound() {
    doThrow(new NotFoundException(ORDER_NOT_FOUND))
        .when(orderService)
        .deleteOrder(orderDto.getOrderUuid());

    NotFoundException exception =
        assertThrows(
            NotFoundException.class, () -> orderController.deleteOrder(orderDto.getOrderUuid()));
    assertEquals(ORDER_NOT_FOUND, exception.getMessage());
    verify(orderService).deleteOrder(orderDto.getOrderUuid());
  }

  @Test
  void updateOrder() {
    when(orderService.updateOrder(orderDto)).thenReturn(orderDto);

    Order result = orderController.updateOrder(updateOrderUuid, orderDto);

    assertEquals(orderDto, result);
    assertEquals(updateOrderUuid, result.getOrderUuid());
    verify(orderService).updateOrder(orderDto);
  }
}
