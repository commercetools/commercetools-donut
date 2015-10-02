package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangePaymentState;
import play.Logger;
import play.libs.F;

import javax.inject.Inject;

import static java.util.Objects.requireNonNull;

@Singleton
public class OrderServiceImpl extends AbstractShopService implements OrderService {

    private static final Logger.ALogger LOG = Logger.of(OrderServiceImpl.class);

    @Inject
    public OrderServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Order> createOrder(final Cart cart) {
        requireNonNull(cart);
        LOG.debug("Creating Order from Cart[cartId={}]", cart.getId());
        final F.Promise<Order> orderPromise = playJavaSphereClient().execute(OrderFromCartCreateCommand.of(cart));
        return orderPromise.flatMap(order -> {
            final ChangePaymentState action = ChangePaymentState.of(PaymentState.PAID);
            return playJavaSphereClient().execute(OrderUpdateCommand.of(order, action));
        });
    }
}