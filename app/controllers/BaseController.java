package controllers;

import play.Configuration;
import play.mvc.Controller;
import utils.CurrencyOperations;

import java.util.Currency;

import static java.util.Objects.requireNonNull;

public abstract class BaseController extends Controller {

    private final CurrencyOperations currencyOps;

    public BaseController(final Configuration configuration) {
        this.currencyOps = CurrencyOperations.of(requireNonNull(configuration, "'configuration' must not be null"));
    }

    protected Currency currency() {
        return currencyOps.currency();
    }
}
