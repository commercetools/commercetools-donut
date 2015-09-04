package controllers;

import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import sphere.Sphere;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BaseController extends Controller {
    public final static String FREQUENCY    = "cart-frequency";
    public final static String ID_MONTHLY   = "pactas4";
    public final static String ID_TWO_WEEKS = "pactas2";
    public final static String ID_WEEKLY    = "pactas1";

    private final Sphere sphere;
    private final CurrencyOperations currencyOps;
    private final ProductProjection productProjection;
    private final SphereClient sphereClient;

    public BaseController(final Sphere sphere, final Configuration configuration, final ProductProjection productProjection,
                          final SphereClient sphereClient) {
        this.sphere = sphere;
        this.currencyOps = CurrencyOperations.of(configuration);
        this.productProjection = productProjection;
        this.sphereClient = sphereClient;
    }

    protected Sphere sphere() {
        return sphere;
    }

    protected SphereClient sphereClient() {
        return sphereClient;
    }

    protected Currency currency() {
        return currencyOps.currency();
    }

    protected ProductProjection productProjection() {
        return productProjection;
    }

    protected Optional<ProductVariant> variant(final int variantId) {
        return productProjection().getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
    }

    protected Optional<ProductVariant> variant(final String pactasId) {
        for(final ProductVariant variant : productProjection().getAllVariants()) {
            final String monthly = variant.getAttribute(ID_MONTHLY).getValue(AttributeAccess.ofString());
            final String twoWeeks = variant.getAttribute(ID_TWO_WEEKS).getValue(AttributeAccess.ofString());
            final String weekly = variant.getAttribute(ID_WEEKLY).getValue(AttributeAccess.ofString());
            if(pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }

    protected int frequency(final String cartId) {
        try {
            final com.google.common.base.Optional<CustomObject> frequencyObj = sphere.customObjects().get(FREQUENCY, cartId).fetch();
            if (frequencyObj.isPresent()) {
                return frequencyObj.get().getValue().asInt();
            }
        } catch (SphereException se) {
            Logger.error(se.getMessage(), se);
        }
        return 0;
    }

    protected Cart clearLineItemsFromCurrentCart(final Cart cart) {
        final List<RemoveLineItem> items = cart.getLineItems().stream().map((item) -> {
            final RemoveLineItem removeLineItem = RemoveLineItem.of(item, 1);
            return removeLineItem;
        }).collect(Collectors.toList());
        final Cart result = sphereClient().execute(CartUpdateCommand.of(cart, items)).toCompletableFuture().join();
        return result;
    }


    protected Cart currentCart() {
        final Http.Session session = session();
        if(session.get("cartId") == null) {
            final Cart cart = sphereClient().execute(CartCreateCommand.of(CartDraft.of(DefaultCurrencyUnits.EUR))).toCompletableFuture().join();
            Logger.debug("Created new Cart[cartId={}]", cart.getId());
            session.put("cartId", cart.getId());
            return cart;
        } else {
            final Cart cart = sphereClient().execute(CartByIdGet.of(session.get("cartId"))).toCompletableFuture().join();
            Logger.debug("Fetched existing Cart[cartId={}]", cart.getId());
            return cart;
        }
    }
}
