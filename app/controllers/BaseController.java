package controllers;

import exceptions.ProductNotFoundException;
import io.sphere.sdk.products.ProductProjection;
import play.Configuration;
import play.mvc.Controller;
import services.CartService;
import services.ProductService;
import utils.CurrencyOperations;

import java.util.Currency;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public abstract class BaseController extends Controller {

    private final CurrencyOperations currencyOps;
    private final ProductService productService;
    private final CartService cartService;

    private final ProductProjection cachedProduct;

    public BaseController(final Configuration configuration, ProductService productService, final CartService cartService) {
        this.productService = requireNonNull(productService, "'productService' must not be null");
        this.cartService = requireNonNull(cartService, "'cartService' must not be null");
        this.currencyOps = CurrencyOperations.of(configuration);
        final Optional<ProductProjection> product = productService().getProduct();
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

    protected ProductService productService() {
        return productService;
    }


    protected Currency currency() {
        return currencyOps.currency();
    }


}
