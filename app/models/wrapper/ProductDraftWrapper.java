package models.wrapper;

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

    public ProductDraft createProductDraft(final Reference<ProductType> productTypeReference,
                                           final Reference<TaxCategory> taxCategoryReference) {

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
                Image.ofWidthAndHeight(imageWrapper.getUrl(), imageWrapper.getDimensions().getWidth(),
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
}
