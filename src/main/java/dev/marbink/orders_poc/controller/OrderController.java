package dev.marbink.orders_poc.controller;

import dev.marbink.orders_poc.dto.GetOrdersResponse;
import dev.marbink.orders_poc.dto.Order;
import dev.marbink.orders_poc.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @GetMapping("/orders")
  public GetOrdersResponse getOrders(
      @RequestParam(name = "query", required = false) String query,
      @RequestParam(name = "date", required = false) LocalDate day) {
    return new GetOrdersResponse(orderService.filterOrders(query, day));
  }

  @PostMapping("/orders")
  public Order createOrder(@Valid @NotNull @RequestBody Order order) {
    // Overriding the orderUuid to null to ensure a new order is created
    order.setOrderUuid(null);
    return orderService.createOrder(order);
  }

  @GetMapping("/orders/{orderUuid}")
  public Order getOrder(@Valid @NotNull @PathVariable(name = "orderUuid") UUID orderUuid) {
    return orderService.getOrder(orderUuid);
  }

  @DeleteMapping("/orders/{orderUuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrder(@Valid @NotNull @PathVariable(name = "orderUuid") UUID orderUuid) {
    orderService.deleteOrder(orderUuid);
  }

  @PutMapping("/orders/{orderUuid}")
  public Order updateOrder(
      @Valid @NotNull @PathVariable(name = "orderUuid") UUID orderUuid,
      @Valid @NotNull @RequestBody Order order) {
    // Overriding the orderUuid to ensure the correct order is updated
    order.setOrderUuid(orderUuid);
    return orderService.updateOrder(order);
  }
}
