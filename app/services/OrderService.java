package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.orders.Order;

public interface OrderService {

    Order createOrder(Cart cart);
}
