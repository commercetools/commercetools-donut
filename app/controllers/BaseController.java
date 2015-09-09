package controllers;

import play.Application;
import play.mvc.Controller;
import utils.CurrencyOperations;

import javax.inject.Inject;
import java.util.Currency;

import static java.util.Objects.requireNonNull;

public abstract class BaseController extends Controller {

    private final CurrencyOperations currencyOps;

    @Inject
    public BaseController(final Application application) {
        final Application app = requireNonNull(application, "'application' must not be null");
        this.currencyOps = CurrencyOperations.of(requireNonNull(app.configuration(), "'configuration' must not be null"));
    }

    protected Currency currency() {
        return currencyOps.currency();
    }
}
