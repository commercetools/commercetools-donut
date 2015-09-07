package controllers;

import play.Configuration;
import play.mvc.Controller;
import utils.CurrencyOperations;

import java.util.Currency;

public abstract class BaseController extends Controller {

    private final CurrencyOperations currencyOps;

    public BaseController(final Configuration configuration) {
        this.currencyOps = CurrencyOperations.of(configuration);
    }

    protected Currency currency() {
        return currencyOps.currency();
    }
}
