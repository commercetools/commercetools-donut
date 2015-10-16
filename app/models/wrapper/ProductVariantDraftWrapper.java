package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ProductVariantDraftWrapper {

    private final String sku;
    private final List<PriceWrapper> prices;
    private final List<AttributeDraftWrapper> attributes;
    private final List<ImageWrapper> images;

    public ProductVariantDraftWrapper(@JsonProperty("sku") final String sku,
                                      @JsonProperty("prices") List<PriceWrapper> prices,
                                      @JsonProperty("attributes") final List<AttributeDraftWrapper> attributes,
                                      @JsonProperty("images") List<ImageWrapper> images) {
        this.sku = requireNonNull(sku);
        this.prices = requireNonNull(prices);
        this.attributes = requireNonNull(attributes);
        this.images = requireNonNull(images);
    }

    public String getSku() {
        return sku;
    }

    public List<PriceWrapper> getPrices() {
        return prices;
    }

    public List<AttributeDraftWrapper> getAttributes() {
        return attributes;
    }

    public List<ImageWrapper> getImages() {
        return images;
    }
}
