package models.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sphere.sdk.models.Base;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.attributes.AttributeDraft;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.javamoney.moneta.Money;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class ProductDraftWrapper extends Base {

    private final LocalizedString name;
    private final LocalizedString description;
    private final LocalizedString slug;
    private final ProductVariantDraftWrapper masterVariant;
    private final List<ProductVariantDraftWrapper> variants;

    public ProductDraftWrapper(@JsonProperty("name") final LocalizedString name,
                               @JsonProperty("description") final LocalizedString description,
                               @JsonProperty("slug") final LocalizedString slug,
                               @JsonProperty("masterVariant") final ProductVariantDraftWrapper masterVariant,
                               @JsonProperty("variants") final List<ProductVariantDraftWrapper> variants) {
        this.name = requireNonNull(name);
        this.description = requireNonNull(description);
        this.slug = requireNonNull(slug);
        this.masterVariant = requireNonNull(masterVariant);
        this.variants = requireNonNull(variants);
        this.variants.add(masterVariant);
    }

    public ProductDraft createProductDraft(final ProductType productType, final TaxCategory taxCategory) {
        final Reference<ProductType> productTypeReference = productType.toReference();
        final Reference<TaxCategory> taxCategoryReference = taxCategory.toReference();

        return ProductDraftBuilder.of(productTypeReference, getName(), getSlug(), smallBox())
                .description(getDescription())
                .taxCategory(taxCategoryReference)
                .variants(Arrays.asList(mediumBox(), largeBox(), hugeBox()))
                .build();
    }

    private ProductVariantDraft smallBox() {
        return createVariant(productVariantDraftWrapper -> "box6".equals(productVariantDraftWrapper.getSku()));
    }

    private ProductVariantDraft mediumBox() {
        return createVariant(productVariantDraftWrapper -> "box12".equals(productVariantDraftWrapper.getSku()));
    }

    private ProductVariantDraft largeBox() {
        return createVariant(productVariantDraftWrapper -> "box24".equals(productVariantDraftWrapper.getSku()));
    }

    private ProductVariantDraft hugeBox() {
        return createVariant(productVariantDraftWrapper -> "box36".equals(productVariantDraftWrapper.getSku()));
    }

    private ProductVariantDraft createVariant(final Predicate<ProductVariantDraftWrapper> boxPredicate) {
        final ProductVariantDraftWrapper variantWrapper = getVariants().stream()
                .filter(boxPredicate).findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find variant"));

        final List<PriceWrapper> pricesWrappers = variantWrapper.getPrices();
        final List<Price> prices = pricesWrappers.stream().map(priceWrapper ->
                PriceBuilder.of(Money.of(priceWrapper.getValue().getAmount(), priceWrapper.getValue().getCurrencyCode()))
                        .build()).collect(Collectors.toList());

        final List<ImageWrapper> imageWrappers = variantWrapper.getImages();
        final List<Image> images = imageWrappers.stream().map(imageWrapper ->
                Image.ofWidthAndHeight(imageWrapper.url, imageWrapper.getDimensions().getWidth(),
                        imageWrapper.getDimensions().getHeight(),
                        "donuts image label")).collect(Collectors.toList());

        final List<AttributeDraftWrapper> attributeDraftWrappers = variantWrapper.getAttributes();
        final List<AttributeDraft> attributes = attributeDraftWrappers.stream()
                .map(attributeDraftWrapper -> AttributeDraft.of(attributeDraftWrapper.getName(),
                        attributeDraftWrapper.getValue()))
                .collect(Collectors.toList());

        return ProductVariantDraftBuilder.of().prices(prices).images(images).sku(variantWrapper.getSku())
                .attributes(attributes).build();
    }

    public LocalizedString getDescription() {
        return description;
    }

    public LocalizedString getName() {
        return name;
    }

    public LocalizedString getSlug() {
        return slug;
    }

    public ProductVariantDraftWrapper getMasterVariant() {
        return masterVariant;
    }

    public List<ProductVariantDraftWrapper> getVariants() {
        return variants;
    }

    public static class ProductVariantDraftWrapper {

        private final String sku;
        private final List<PriceWrapper> prices;
        private final List<AttributeDraftWrapper> attributes;
        private final List<ImageWrapper> images;
        private final AvailabilityWrapper availability;

        public ProductVariantDraftWrapper(@JsonProperty("sku") final String sku,
                                          @JsonProperty("prices") List<PriceWrapper> prices,
                                          @JsonProperty("attributes") final List<AttributeDraftWrapper> attributes,
                                          @JsonProperty("images") List<ImageWrapper> images,
                                          @JsonProperty("availability") final AvailabilityWrapper availability) {
            this.sku = requireNonNull(sku);
            this.prices = requireNonNull(prices);
            this.attributes = requireNonNull(attributes);
            this.images = requireNonNull(images);
            this.availability = requireNonNull(availability);
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

        public AvailabilityWrapper getAvailability() {
            return availability;
        }
    }

    public static class ImageWrapper {

        private final String url;
        private final ImageDimensionsWrapper dimensions;

        public ImageWrapper(@JsonProperty("url") final String url,
                            @JsonProperty("dimensions") final ImageDimensionsWrapper dimensions) {
            this.url = requireNonNull(url);
            this.dimensions = requireNonNull(dimensions);
        }

        public String getUrl() {
            return url;
        }

        public ImageDimensionsWrapper getDimensions() {
            return dimensions;
        }
    }

    public static class ImageDimensionsWrapper {

        private final Integer width;
        private final Integer height;

        public ImageDimensionsWrapper(@JsonProperty("w") final Integer width, @JsonProperty("h") final Integer height) {
            this.width = requireNonNull(width);
            this.height = requireNonNull(height);
        }

        public Integer getWidth() {
            return width;
        }

        public Integer getHeight() {
            return height;
        }
    }

    public static class AttributeDraftWrapper {

        private final String name;
        private final Object value;

        public AttributeDraftWrapper(@JsonProperty("name") final String name, @JsonProperty("value") final Object value) {
            this.name = requireNonNull(name);
            this.value = requireNonNull(value);
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class AvailabilityWrapper {

        private final Boolean isOnStock;

        public AvailabilityWrapper(@JsonProperty("typeId") final Boolean isOnStock) {
            this.isOnStock = isOnStock;
        }
    }


    public static class ProductTypeWrapper {

        private final String typeId;
        private final String id;

        public ProductTypeWrapper(@JsonProperty("typeId") final String typeId, @JsonProperty("id") final String id) {
            this.typeId = requireNonNull(typeId);
            this.id = requireNonNull(id);
        }
    }

    public static class PriceWrapper {

        private final PriceValueWrapper value;

        public PriceWrapper(@JsonProperty("value") final PriceValueWrapper value) {
            this.value = requireNonNull(value);
        }

        public PriceValueWrapper getValue() {
            return value;
        }
    }

    public static class PriceValueWrapper {

        private final Double amount;
        private final String currencyCode;

        public PriceValueWrapper(@JsonProperty("amount") final Double amount,
                                 @JsonProperty("currencyCode") final String currencyCode) {
            this.amount = requireNonNull(amount);
            this.currencyCode = requireNonNull(currencyCode);
        }

        public Double getAmount() {
            return amount;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }
    }
}
