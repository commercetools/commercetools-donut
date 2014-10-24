package controllers;

import com.google.common.base.Optional;
import com.neovisionaries.i18n.CountryCode;
import exceptions.DefaultCountryNotFound;
import exceptions.DefaultCurrencyNotFound;
import play.Configuration;
import play.Logger;

import java.util.Currency;

public class GlobalOperations {
    static final String COUNTRY_CONFIG = "sphere.cart.country";
    static final String CURRENCY_CONFIG = "sphere.cart.currency";
    public static final Logger.ALogger LOGGER = Logger.of(GlobalOperations.class);
    private final Configuration configuration;

    private GlobalOperations(Configuration configuration) {
        this.configuration = configuration;
    }

    public static GlobalOperations of(Configuration configuration) {
        return new GlobalOperations(configuration);
    }

    /**
     * Gets the currency for this shop, as defined in the configuration file.
     * @return the currency for this shop.
     */
    public Currency currency() {
        final Optional<Currency> currencyCode = parseCurrencyCode(configCurrency());
        if (currencyCode.isPresent()) {
            return currencyCode.get();
        } else {
            throw new DefaultCurrencyNotFound();
        }
    }

    /**
     * Gets the country for this shop, as defined in the configuration file.
     * @return the country for this shop.
     */
    public CountryCode country() {
        final Optional<CountryCode> countryCode = parseCountryCode(configCountry());
        if (countryCode.isPresent()) {
            return countryCode.get();
        } else {
            throw new DefaultCountryNotFound();
        }
    }

    /**
     * Parses a currency code as string.
     * @param currencyCodeAsString the string representing a currency code.
     * @return the currency code represented in the string, or absent if it does not correspond to a valid currency.
     */
    public static Optional<Currency> parseCurrencyCode(String currencyCodeAsString) {
        try {
            return Optional.of(Currency.getInstance(currencyCodeAsString));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid currency " + currencyCodeAsString);
            return Optional.absent();
        }
    }

    /**
     * Parses a country code as string.
     * @param countryCodeAsString the string representing a country code.
     * @return the country code represented in the string, or absent if it does not correspond to a valid country.
     */
    public static Optional<CountryCode> parseCountryCode(String countryCodeAsString) {
        try {
            return Optional.of(CountryCode.valueOf(countryCodeAsString));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid country " + countryCodeAsString);
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

    /**
     * Gets the country for this project.
     * @return the country as defined in the configuration file, or empty string if none defined.
     */
    private String configCountry() {
        return configuration.getString(COUNTRY_CONFIG, "");
    }
}
