package controllers;

import io.sphere.sdk.products.ProductProjection;
import play.Application;
import play.mvc.Controller;
import utils.CurrencyOperations;

import javax.inject.Inject;
import java.util.Currency;

import static java.util.Objects.requireNonNull;

public abstract class BaseController extends Controller {

    private final CurrencyOperations currencyOps;
    private final ProductProjection productProjection;

    @Inject
    public BaseController(final Application application, final ProductProjection productProjection) {
        final Application app = requireNonNull(application);
        this.currencyOps = CurrencyOperations.of(requireNonNull(app.configuration()));
        this.productProjection = requireNonNull(productProjection);
    }

    protected Currency currency() {
        return currencyOps.currency();
    }

    protected ProductProjection productProjection() {
        return productProjection;
    }
}
