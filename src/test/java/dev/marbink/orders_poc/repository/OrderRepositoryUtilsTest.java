package dev.marbink.orders_poc.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import dev.marbink.orders_poc.entity.Order;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryUtilsTest {

  @Mock CriteriaBuilder criteriaBuilder;
  @Mock Root<Order> root;
  @Mock Path<String> stringExpression;
  @Mock Predicate predicate;

  @Test
  void notMandatoryDescriptionContainsIgnoreCaseNull() {
    Specification<Order> specification =
        OrderRepositoryUtils.notMandatoryDescriptionContainsIgnoreCase(null);
    assertNull(specification.toPredicate(root, null, criteriaBuilder));
  }

  @Test
  void notMandatoryCreationDateNull() {
    Specification<Order> specification = OrderRepositoryUtils.notMandatoryCreationDate(null);
    assertNull(specification.toPredicate(root, null, criteriaBuilder));
  }

  @Test
  void notMandatoryDescriptionContainsIgnoreCase() {
    when(criteriaBuilder.lower(any())).thenReturn(stringExpression);
    when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(predicate);
    Specification<Order> specification =
        OrderRepositoryUtils.notMandatoryDescriptionContainsIgnoreCase("test");
    assertNotNull(specification.toPredicate(root, null, criteriaBuilder));
  }

  @Test
  void notMandatoryCreationDate() {
    when(criteriaBuilder.between(any(), any(LocalDateTime.class), any(LocalDateTime.class)))
        .thenReturn(predicate);
    Specification<Order> specification =
        OrderRepositoryUtils.notMandatoryCreationDate(LocalDateTime.now().toLocalDate());
    assertNotNull(specification.toPredicate(root, null, criteriaBuilder));
  }
}
