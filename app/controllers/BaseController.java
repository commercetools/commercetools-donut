package controllers;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.CartUpdate;
import io.sphere.client.shop.model.LineItem;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.carts.queries.CartQueryModel;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.Query;
import io.sphere.sdk.queries.QueryPredicate;
import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import sphere.Session;
import sphere.Sphere;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

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

    protected void clearLineItemsFromCurrentCart(final List<LineItem> lineItems) {
        CartUpdate cartUpdate = new CartUpdate();
        for (final LineItem item : lineItems) {
            cartUpdate = cartUpdate.removeLineItem(item.getId());
        }
        sphere.currentCart().update(cartUpdate);
    }


    protected Optional<ProductVariant> mapToProductVariant(final Optional<Variant> variant) {
        if(variant.isPresent()) {
            final int variantId = variant.get().getId();
            return variant(variantId);
        }
        return Optional.empty();
    }

    protected Cart currentCart() {
        final Session session = Session.current();
        if(session.getCartId() == null) {
            final CartDraft cartDraft = CartDraft.of(DefaultCurrencyUnits.EUR).withCountry(CountryCode.DE);
            final Cart cart = sphereClient().execute(CartCreateCommand.of(cartDraft)).toCompletableFuture().join();
            Logger.debug("Created new Cart[cartId={}]", cart.getId());
            return cart;
        } else {
            final String cartId = session.getCartId().getId();
            final QueryPredicate<Cart> predicate = CartQueryModel.of().id().is(cartId);
            final Query<Cart> cartQuery = CartQuery.of().withPredicates(predicate);
            final CompletionStage<PagedQueryResult<Cart>> resultCompletionStage = sphereClient().execute(cartQuery);
            final PagedQueryResult<Cart> queryResult = resultCompletionStage.toCompletableFuture().join();
            final Optional<Cart> cart = queryResult.head();
            Logger.debug("Fetched existing Cart[cartId={}]", cart.get().getId());
            return cart.get();
        }
    }
}
