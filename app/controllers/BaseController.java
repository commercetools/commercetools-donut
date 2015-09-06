package controllers;

import exceptions.ProductNotFoundException;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeAccess;
import play.Configuration;
import play.mvc.Controller;
import services.CartService;
import utils.CurrencyOperations;

import java.util.Currency;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public abstract class BaseController extends Controller {
    public final static String FREQUENCY    = "cart-frequency";
    public final static String ID_MONTHLY   = "pactas4";
    public final static String ID_TWO_WEEKS = "pactas2";
    public final static String ID_WEEKLY    = "pactas1";

    private final CurrencyOperations currencyOps;
    private final CartService cartService;

    private final ProductProjection cachedProduct;

    public BaseController(final Configuration configuration, final CartService cartService) {
        this.cartService = requireNonNull(cartService, "'cartService' must not be null");
        this.currencyOps = CurrencyOperations.of(configuration);
        final Optional<ProductProjection> product = cartService().getProduct();
        if(!product.isPresent()) {
            throw new ProductNotFoundException();
        }
        this.cachedProduct = product.get();
    }

    protected ProductProjection product() {
        return cachedProduct;
    }

    protected CartService cartService() {
        return cartService;
    }

    protected Currency currency() {
        return currencyOps.currency();
    }

    protected Optional<ProductVariant> variant(final int variantId) {
        return product().getAllVariants().stream().filter(v -> v.getId().equals(variantId)).findFirst();
    }

    protected Optional<ProductVariant> variant(final String pactasId) {
        for(final ProductVariant variant : product().getAllVariants()) {
            final String monthly = variant.getAttribute(ID_MONTHLY).getValue(AttributeAccess.ofString());
            final String twoWeeks = variant.getAttribute(ID_TWO_WEEKS).getValue(AttributeAccess.ofString());
            final String weekly = variant.getAttribute(ID_WEEKLY).getValue(AttributeAccess.ofString());
            if(pactasId.equals(monthly) || pactasId.equals(twoWeeks) || pactasId.equals(weekly)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }
}
