package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangePaymentState;
import play.Logger;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@Singleton
public class OrderServiceImpl extends AbstractShopService implements OrderService {

    private static final Logger.ALogger LOG = Logger.of(OrderServiceImpl.class);

    @Inject
    public OrderServiceImpl(final SphereClient sphereClient) {
        super(sphereClient);
    }

    @Override
    public CompletionStage<Order> createOrder(final Cart cart) {
        LOG.debug("Creating Order from Cart[cartId={}]", cart.getId());
        final CompletionStage<Order> orderPromise = sphereClient().execute(OrderFromCartCreateCommand.of(cart));
        return orderPromise.thenCompose(order -> {
            final ChangePaymentState action = ChangePaymentState.of(PaymentState.PAID);
            return sphereClient().execute(OrderUpdateCommand.of(order, action));
        });
    }
}