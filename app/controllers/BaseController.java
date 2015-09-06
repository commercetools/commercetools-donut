package controllers;

import exceptions.ProductNotFoundException;
import io.sphere.sdk.products.ProductProjection;
import play.Configuration;
import play.mvc.Controller;
import services.ShopService;
import utils.CurrencyOperations;

import java.util.Currency;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public abstract class BaseController extends Controller {

    private final CurrencyOperations currencyOps;
    private final ShopService shopService;

    private final ProductProjection cachedProduct;

    public BaseController(final Configuration configuration, final ShopService cartService) {
        this.shopService = requireNonNull(cartService, "'shopService' must not be null");
        this.currencyOps = CurrencyOperations.of(configuration);
        final Optional<ProductProjection> product = shopService().getProduct();
        if(!product.isPresent()) {
            throw new ProductNotFoundException();
        }
        this.cachedProduct = product.get();
    }

    protected ProductProjection product() {
        return cachedProduct;
    }

    protected ShopService shopService() {
        return shopService;
    }

    protected Currency currency() {
        return currencyOps.currency();
    }


}
