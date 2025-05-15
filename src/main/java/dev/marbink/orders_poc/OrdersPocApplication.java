package dev.marbink.orders_poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class OrdersPocApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrdersPocApplication.class, args);
  }
}
