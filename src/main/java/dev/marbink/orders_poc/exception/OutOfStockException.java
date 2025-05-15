package dev.marbink.orders_poc.exception;

public class OutOfStockException extends RuntimeException {

  public OutOfStockException(String message) {
    super(message);
  }
}
