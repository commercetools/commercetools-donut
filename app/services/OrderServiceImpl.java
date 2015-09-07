package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangePaymentState;

import static java.util.Objects.requireNonNull;

public class OrderServiceImpl implements OrderService{

    private final SphereClient sphereClient;

    public OrderServiceImpl(final SphereClient sphereClient) {
        this.sphereClient = requireNonNull(sphereClient, "'sphereClient' must not be null");
    }

    @Override
    public Order createOrder(final Cart cart) {
        final Order order = sphereClient.execute(OrderFromCartCreateCommand.of(cart)).toCompletableFuture().join();
        final ChangePaymentState action = ChangePaymentState.of(PaymentState.PAID);
        final Order updatedOrder = sphereClient.execute(OrderUpdateCommand.of(order, action)).toCompletableFuture().join();
        return updatedOrder;
    }
}
