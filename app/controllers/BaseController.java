package controllers;

import com.google.common.base.Optional;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.CartUpdate;
import io.sphere.client.shop.model.LineItem;
import io.sphere.client.shop.model.Variant;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
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
    private final CurrencyOperations currencyOps;

    private final SphereClient sphereClient;
    private final ProductProjection productProjection;

    public BaseController(final Sphere sphere, final Configuration configuration, final SphereClient sphereClient,
                          final ProductProjection productProjection) {
        this.sphere = sphere;
        this.currencyOps = CurrencyOperations.of(configuration);
        this.sphereClient = sphereClient;
        this.productProjection = productProjection;
    }

    protected Sphere sphere() {
        return sphere;
    }

    protected Currency currency() {
        return currencyOps.currency();
    }

    protected SphereClient sphereClient() {
        return sphereClient;
    }

    protected ProductProjection productProjection() {
        return productProjection;
    }

    protected java.util.Optional<ProductVariant> variant(final int variantId) {
        return productProjection().getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
    }

    protected java.util.Optional<ProductVariant> variant(final String pactasId) {
        for(final ProductVariant variant : productProjection().getAllVariants()) {
            final String monthly = variant.getAttribute(ID_MONTHLY).getValue(AttributeAccess.ofString());
            final String twoWeeks = variant.getAttribute(ID_TWO_WEEKS).getValue(AttributeAccess.ofString());
            final String weekly = variant.getAttribute(ID_WEEKLY).getValue(AttributeAccess.ofString());
            if(pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) {
                return java.util.Optional.of(variant);
            }
        }
        return java.util.Optional.empty();
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


    protected java.util.Optional<ProductVariant> mapToProductVariant(final java.util.Optional<Variant> variant) {
        if(variant.isPresent()) {
            final Variant var = variant.get();
            final int variantId = var.getId();
            return productProjection().getAllVariants().stream().filter( v -> v.getId().equals(variantId)).findFirst();
        }
        return java.util.Optional.empty();
    }
}
