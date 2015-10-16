package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.taxcategories.TaxCategoryDraft;
import io.sphere.sdk.taxcategories.TaxRate;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class TaxCategoryWrapper {

    private final String name;
    private final List<TaxRateWrapper> rates;

    public TaxCategoryWrapper(@JsonProperty("name") final String name,
                              @JsonProperty("rates") final List<TaxRateWrapper> rates) {
        this.name = requireNonNull(name);
        this.rates = requireNonNull(rates);
    }

    public TaxCategoryDraft createTaxCategoryDraft() {
        final List<TaxRate> rates = getRates().stream()
                .map(taxRateWrapper -> TaxRate.of(taxRateWrapper.getName(), taxRateWrapper.getAmount(),
                        taxRateWrapper.getIncludedInPrice(), taxRateWrapper.getCountry()))
                .collect(Collectors.toList());
        return TaxCategoryDraft.of(name, rates);
    }

    public String getName() {
        return name;
    }

    public List<TaxRateWrapper> getRates() {
        return rates;
    }

    public static class TaxRateWrapper {

        private final String name;
        private final Double amount;
        private final Boolean includedInPrice;
        private final CountryCode country;


        public TaxRateWrapper(@JsonProperty("name") final String name, @JsonProperty("amount") final Double amount,
                              @JsonProperty("includedInPrice") final Boolean includedInPrice,
                              @JsonProperty("country") final CountryCode country) {
            this.name = name;
            this.amount = amount;
            this.includedInPrice = includedInPrice;
            this.country = country;
        }

        public String getName() {
            return name;
        }

        public Double getAmount() {
            return amount;
        }

        public Boolean getIncludedInPrice() {
            return includedInPrice;
        }

        public CountryCode getCountry() {
            return country;
        }
    }
}
