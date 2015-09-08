package utils;

import com.google.common.base.Optional;
import exceptions.DefaultCurrencyNotFound;
import play.Configuration;
import play.Logger;

import java.util.Currency;

public class CurrencyOperations {
    public static final Logger.ALogger LOGGER = Logger.of(CurrencyOperations.class);
    public static final String CURRENCY_CONFIG = "sphere.cart.currency";
    private final Configuration configuration;

    private CurrencyOperations(final Configuration configuration) {
        this.configuration = configuration;
    }

    public static CurrencyOperations of(final Configuration configuration) {
        return new CurrencyOperations(configuration);
    }

    /**
     * Gets the currency for this shop, as defined in the configuration file.
     * @return the currency for this shop.
     */
    public Currency currency() {
        final Optional<Currency> currencyCode = parseCode(configCurrency());
        if (currencyCode.isPresent()) {
            return currencyCode.get();
        } else {
            throw new DefaultCurrencyNotFound();
        }
    }

    /**
     * Parses a currency code as string.
     * @param currencyCodeAsString the string representing a currency code.
     * @return the currency code represented in the string, or absent if it does not correspond to a valid currency.
     */
    public static Optional<Currency> parseCode(String currencyCodeAsString) {
        try {
            return Optional.of(Currency.getInstance(currencyCodeAsString));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid currency " + currencyCodeAsString);
            return Optional.absent();
        }
    }



    /**
     * Gets the currency for this project.
     * @return the currency as defined in the configuration file, or empty string if none defined.
     */
    private String configCurrency() {
        return configuration.getString(CURRENCY_CONFIG, "");
    }
}

