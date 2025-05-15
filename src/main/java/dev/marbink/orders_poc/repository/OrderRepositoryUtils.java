package dev.marbink.orders_poc.repository;

import dev.marbink.orders_poc.entity.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

public class OrderRepositoryUtils {
  public static Specification<Order> notMandatoryDescriptionContainsIgnoreCase(String input) {
    return (Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (input == null) {
        return null;
      }
      String pattern = "%" + input.toLowerCase() + "%";
      return cb.like(cb.lower(root.get("description")), pattern);
    };
  }

  public static Specification<Order> notMandatoryCreationDate(LocalDate day) {
    return (Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      if (day == null) {
        return null;
      }
      return cb.between(
          root.get("creationDate"), day.atStartOfDay(), day.plusDays(1).atStartOfDay());
    };
  }
}
