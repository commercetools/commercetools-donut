package services;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.ChangePaymentState;
import play.Logger;
import play.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Singleton
public class OrderServiceImpl extends AbstractShopService implements OrderService {

    private static final Logger.ALogger LOG = Logger.of(OrderServiceImpl.class);

    @Inject
    public OrderServiceImpl(final SphereClient sphereClient,  final ApplicationLifecycle applicationLifecycle,
                            final PlayJavaSphereClient playJavaSphereClient) {
        super(sphereClient, applicationLifecycle, playJavaSphereClient);
    }

    @Override
    public Order createOrder(final Cart cart) {
        requireNonNull(cart);
        LOG.debug("Creating Order from Cart[cartId={}]", cart.getId());
        return Optional.of(sphereClient().execute(OrderFromCartCreateCommand.of(cart)).toCompletableFuture().join())
                .map((order) -> {
                    final ChangePaymentState action = ChangePaymentState.of(PaymentState.PAID);
                    return sphereClient().execute(OrderUpdateCommand.of(order, action)).toCompletableFuture().join();
                }).orElseThrow(() -> new RuntimeException("unbale to create Order"));
    }
}
