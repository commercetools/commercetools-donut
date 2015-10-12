package models.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.LocalizedString;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ProductDraftWrapper extends Base {

    private ProductTypeWrapper productType;
    private final LocalizedString name;
    private final LocalizedString slug;
    private final ProductVariantDraftWrapper masterVariant;
    private final List<ProductVariantDraftWrapper> variants;

    public ProductDraftWrapper(@JsonProperty("name") final LocalizedString name,
                               @JsonProperty("slug") final LocalizedString slug,
                               @JsonProperty("masterVariant") final ProductVariantDraftWrapper masterVariant,
                               @JsonProperty("variants") final List<ProductVariantDraftWrapper> variants) {

        this.name = requireNonNull(name);
        this.slug = requireNonNull(slug);
        this.masterVariant = requireNonNull(masterVariant);
        this.variants = requireNonNull(variants);
    }

    public static class ProductVariantDraftWrapper {

        private final String sku;

        public ProductVariantDraftWrapper(final String sku) {
            this.sku = requireNonNull(sku);
        }
    }

    public static class ProductTypeWrapper {

        private final String name;

        public ProductTypeWrapper(@JsonProperty("name") final String name) {
            this.name = requireNonNull(name);
        }
    }

    public static class PriceWrapper {

        private final PriceValueWrapper value;

        public PriceWrapper(@JsonProperty("value") final PriceValueWrapper value) {
            this.value = requireNonNull(value);
        }
    }

    public static class PriceValueWrapper {

        private final Long centAmount;
        private final String currencyCode;

        public PriceValueWrapper(@JsonProperty("centAmount") final Long centAmount,
                                 @JsonProperty("currencyCode") final String currencyCode) {
            this.centAmount = requireNonNull(centAmount);
            this.currencyCode = requireNonNull(currencyCode);
        }
    }
}
