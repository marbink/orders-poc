package dev.marbink.orders_poc.mapper;

import dev.marbink.orders_poc.entity.Order;
import dev.marbink.orders_poc.entity.OrderProduct;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  @Mapping(target = "products", source = "orderProducts")
  dev.marbink.orders_poc.dto.Order toOrderDto(Order order);

  @Mapping(target = "productUuid", source = "product.productUuid")
  dev.marbink.orders_poc.dto.Product toProductDto(OrderProduct orderProduct);

  Order toOrder(dev.marbink.orders_poc.dto.Order orderDto);
}
