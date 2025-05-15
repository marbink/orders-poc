package dev.marbink.orders_poc.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
  @CreatedDate
  @Column(name = "creation_date", nullable = false, updatable = false)
  private LocalDateTime creationDate;

  @LastModifiedDate
  @Column(name = "last_update_date", nullable = false)
  private LocalDateTime lastUpdateDate;
}
