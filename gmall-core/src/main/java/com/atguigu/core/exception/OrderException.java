package com.atguigu.core.exception;

/**
 * @author lzzzzz
 * @create 2020-02-09 17:32
 */
public class OrderException extends RuntimeException {
    public OrderException() {
        super();
    }

    public OrderException(String message) {
        super(message);
    }
}
