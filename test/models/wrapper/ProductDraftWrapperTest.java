package models.wrapper;

import io.sphere.sdk.models.DefaultCurrencyUnits;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.products.ProductVariantDraft;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.junit.Before;
import org.junit.Test;
import utils.JsonUtils;

import javax.money.MonetaryAmount;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProductDraftWrapperTest {

    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";

    private Reference<ProductType> productTypeReference;
    private Reference<TaxCategory> taxCategoryReference;
    private ProductDraft productDraft;

    @Before
    public void setUp() {
        productTypeReference = Reference.of("product-type", UUID.randomUUID().toString());
        taxCategoryReference = Reference.of("tax-category", UUID.randomUUID().toString());
        final ProductDraftWrapper productDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE, ProductDraftWrapper.class);
        assertThat(productDraftWrapper).isNotNull();
        productDraft = productDraftWrapper.createProductDraft(productTypeReference, taxCategoryReference);
    }

    @Test
    public void testCreateProductDraft() throws Exception {
        assertThat(productDraft).isNotNull();
        assertThat(productDraft.getName()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Classic box"));
        assertThat(productDraft.getProductType().getTypeId()).isEqualTo(productTypeReference.getTypeId());
        assertThat(productDraft.getProductType().getId()).isEqualTo(productTypeReference.getId());
        assertThat(productDraft.getTaxCategory().getTypeId()).isEqualTo(taxCategoryReference.getTypeId());
        assertThat(productDraft.getTaxCategory().getId()).isEqualTo(taxCategoryReference.getId());
        assertThat(productDraft.getSlug()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "donut-box"));
        assertThat(productDraft.getMasterVariant().getSku()).isEqualTo("box6");
        assertThat(productDraft.getVariants().size()).isEqualTo(3);
    }

    @Test
    public void testCreateSmallBox() {
        final ProductVariantDraft smallBox = productDraft.getMasterVariant();
        assertThat(smallBox.getSku()).isEqualTo("box6");
        assertThat(smallBox.getPrices().size()).isEqualTo(1);
        final MonetaryAmount amount = smallBox.getPrices().get(0).getValue();
        assertThat(amount.getNumber().doubleValueExact()).isEqualTo(6.99);
        assertThat(amount.getCurrency()).isEqualTo(DefaultCurrencyUnits.EUR);
        assertThat(smallBox.getImages().size()).isEqualTo(3);
        assertThat(smallBox.getAttributes().size()).isEqualTo(5);
    }

    @Test
    public void testCreateMediumBox() {
        final Optional<ProductVariantDraft> optionalMediumBox = productDraft.getVariants().stream()
                .filter(productVariantDraft -> "box12".equals(productVariantDraft.getSku())).findFirst();
        assertThat(optionalMediumBox.isPresent());

        final ProductVariantDraft mediumBox = optionalMediumBox.get();
        assertThat(mediumBox.getPrices().size()).isEqualTo(1);
        final MonetaryAmount amount = mediumBox.getPrices().get(0).getValue();
        assertThat(amount.getNumber().doubleValueExact()).isEqualTo(12.99);
        assertThat(amount.getCurrency()).isEqualTo(DefaultCurrencyUnits.EUR);
        assertThat(mediumBox.getImages().size()).isEqualTo(3);
        assertThat(mediumBox.getAttributes().size()).isEqualTo(5);
    }

    @Test
    public void testCreateLargeBox() {
        final Optional<ProductVariantDraft> optionalLargeBox = productDraft.getVariants().stream()
                .filter(productVariantDraft -> "box24".equals(productVariantDraft.getSku())).findFirst();
        assertThat(optionalLargeBox.isPresent());

        final ProductVariantDraft largeBox = optionalLargeBox.get();
        assertThat(largeBox.getPrices().size()).isEqualTo(1);
        final MonetaryAmount amount = largeBox.getPrices().get(0).getValue();
        assertThat(amount.getNumber().doubleValueExact()).isEqualTo(23.99);
        assertThat(amount.getCurrency()).isEqualTo(DefaultCurrencyUnits.EUR);
        assertThat(largeBox.getImages().size()).isEqualTo(3);
        assertThat(largeBox.getAttributes().size()).isEqualTo(5);
    }

    @Test
    public void testCreateHugeBox() {
        final Optional<ProductVariantDraft> optionalHugeBox = productDraft.getVariants().stream()
                .filter(productVariantDraft -> "box36".equals(productVariantDraft.getSku())).findFirst();
        assertThat(optionalHugeBox.isPresent());

        final ProductVariantDraft hugeBox = optionalHugeBox.get();
        assertThat(hugeBox.getPrices().size()).isEqualTo(1);
        final MonetaryAmount amount = hugeBox.getPrices().get(0).getValue();
        assertThat(amount.getNumber().doubleValueExact()).isEqualTo(34.99);
        assertThat(amount.getCurrency()).isEqualTo(DefaultCurrencyUnits.EUR);
        assertThat(hugeBox.getImages().size()).isEqualTo(3);
        assertThat(hugeBox.getAttributes().size()).isEqualTo(5);
    }
}
