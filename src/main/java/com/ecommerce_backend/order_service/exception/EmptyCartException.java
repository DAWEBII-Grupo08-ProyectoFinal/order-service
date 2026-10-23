package com.ecommerce_backend.order_service.exception;

public class EmptyCartException extends RuntimeException {
    public EmptyCartException() {
        super("No se puede crear un pedido con el carrito vacio");
    }
}
