package models.wrapper;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.junit.Test;
import utils.JsonUtils;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProductDraftWrapperTest {

    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";

    @Test
    public void testCreateProductDraft() throws Exception {
        final ProductDraftWrapper productDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE, ProductDraftWrapper.class);
        assertThat(productDraftWrapper).isNotNull();

        final Reference<ProductType> productTypeReference = Reference.of("product-type", UUID.randomUUID().toString());
        final Reference<TaxCategory> taxCategoryReference = Reference.of("tax-category", UUID.randomUUID().toString());

        final ProductDraft productDraft = productDraftWrapper.createProductDraft(productTypeReference, taxCategoryReference);
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
}
