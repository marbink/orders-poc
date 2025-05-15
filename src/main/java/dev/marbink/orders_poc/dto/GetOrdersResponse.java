package dev.marbink.orders_poc.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@AllArgsConstructor
public class GetOrdersResponse {
  List<Order> items;
}
