package controllers;

import com.google.common.base.Optional;
import exceptions.SubscriptionProductNotFound;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.CartUpdate;
import io.sphere.client.shop.model.LineItem;
import io.sphere.client.shop.model.Product;
import io.sphere.client.shop.model.Variant;
import play.Configuration;
import play.Logger;
import play.mvc.Controller;
import sphere.Sphere;

import java.util.Currency;
import java.util.List;

public class BaseController extends Controller {
    public final static String FREQUENCY    = "cart-frequency";
    public final static String ID_MONTHLY   = "pactas4";
    public final static String ID_TWO_WEEKS = "pactas2";
    public final static String ID_WEEKLY    = "pactas1";

    private final Sphere sphere;
    private final Product product;
    private final CurrencyOperations currencyOps;

    public BaseController(final Sphere sphere, final Configuration configuration, final Product product) {
        this.sphere = sphere;
        this.product = product;
        this.currencyOps = CurrencyOperations.of(configuration);
    }

    protected Sphere sphere() {
        return sphere;
    }

    protected Currency currency() {
        return currencyOps.currency();
    }

    protected Product product() {
        return product;
    }

    protected Optional<Variant> variant(final int variantId) {
        return product().getVariants().byId(variantId);
    }

    protected Optional<Variant> variant(final String pactasId) {
        for (final Variant variant : product().getVariants().asList()) {
            if (pactasId.equals(variant.getString(ID_MONTHLY))
                    || pactasId.equals(variant.getString(ID_TWO_WEEKS))
                    || pactasId.equals(variant.getString(ID_WEEKLY))) {
                return Optional.of(variant);
            }
        }
        return Optional.absent();
    }

    protected int frequency(final String cartId) {
        try {
            final Optional<CustomObject> frequencyObj = sphere.customObjects().get(FREQUENCY, cartId).fetch();
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
}
