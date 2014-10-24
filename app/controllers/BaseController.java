package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.neovisionaries.i18n.CountryCode;
import exceptions.SubscriptionProductNotFound;
import io.sphere.client.exceptions.SphereException;
import io.sphere.client.model.CustomObject;
import io.sphere.client.shop.model.*;
import play.Configuration;
import play.mvc.Controller;
import sphere.Session;
import sphere.Sphere;

import java.util.Currency;

import static utils.JsonUtils.convertToNewFormat;

public class BaseController extends Controller {
    public final static String FREQUENCY    = "cart-frequency";
    public final static String PRODUCT_SLUG = "donut-box";
    public final static String ID_MONTHLY   = "pactas4";
    public final static String ID_TWO_WEEKS = "pactas2";
    public final static String ID_WEEKLY    = "pactas1";

    private final Sphere sphere;
    private final Configuration configuration;
    private final GlobalOperations globalOperations;

    public BaseController(Sphere sphere, Configuration configuration) {
        this.sphere = sphere;
        this.configuration = configuration;
        this.globalOperations = GlobalOperations.of(configuration);
    }

    protected Sphere sphere() {
        return sphere;
    }

    protected Product product() {
        final Optional<Product> product = sphere.products().bySlug(PRODUCT_SLUG).fetch();
        if (product.isPresent()) {
            return product.get();
        } else {
            throw new SubscriptionProductNotFound();
        }
    }

    protected Optional<Variant> variant(int variantId) {
        return product().getVariants().byId(variantId);
    }

    public Optional<Variant> variant(String pactasId) {
        for (Variant variant : product().getVariants().asList()) {
            if (pactasId.equals(variant.getString(ID_MONTHLY))
                    || pactasId.equals(variant.getString(ID_TWO_WEEKS))
                    || pactasId.equals(variant.getString(ID_WEEKLY))) {
                return Optional.of(variant);
            }
        }
        return Optional.absent();
    }

    public Optional<Integer> getFrequency(String key) {
        try {
            final Optional<CustomObject> frequencyObj = sphere.customObjects().get(FREQUENCY, key).fetch();
            if (frequencyObj.isPresent()) {
                final JsonNode frequencyNode = convertToNewFormat(frequencyObj.get().getValue());
                return Optional.of(frequencyNode.asInt(0));
            } else {
                return Optional.absent();
            }
        } catch (SphereException se) {
            return Optional.absent();
        }
    }

    public void setFrequency(String key, int frequency) {
        sphere.customObjects().set(FREQUENCY, key, frequency);
    }

    public void unsetFrequency(String key) {
        try {
            sphere.customObjects().delete(FREQUENCY, key).execute();
        } catch (SphereException se) {
            // Assume it is already unset
        }
    }

    public void addLineItem(Variant variant, int frequency) {
        clearLineItems(sphere().currentCart().fetch());
        Cart cart = sphere().currentCart().addLineItem(getProduct().getId(), variant.getId(), 1);
        setFrequency(cart.getId(), frequency);
    }


    public static Cart clearLineItems(Cart cart) {
        CartUpdate update = new CartUpdate();
        for (LineItem item : cart.getLineItems()) {
            update.removeLineItem(item.getId());
        }
        return sphere().currentCart().update(update);
    }

    public void clearCart() {
        Cart cart = sphere.currentCart().fetch();
        unsetFrequency(cart.getId());
        Session.current().clearCart();
    }

}
