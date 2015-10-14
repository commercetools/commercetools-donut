package models.export;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.taxcategories.TaxCategory;
import org.junit.Before;
import org.junit.Test;
import utils.JsonUtils;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductDraftWrapperTest {

    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";

    private ProductType productType;
    private TaxCategory taxCategory;

    @Before
    public void setUp() {
        productType = mock(ProductType.class);
        final String productTypeId = UUID.randomUUID().toString();
        when(productType.toReference()).thenReturn(Reference.of("product-type", productTypeId, productType));
        when(productType.getId()).thenReturn(productTypeId);

        taxCategory = mock(TaxCategory.class);
        final String taxCategoryId = UUID.randomUUID().toString();
        when(taxCategory.toReference()).thenReturn(Reference.of("tax-category", taxCategoryId, taxCategory));
        when(taxCategory.getId()).thenReturn(taxCategoryId);
    }

    @Test
    public void testCreateProductDraft() throws Exception {
        final ProductDraftWrapper productDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE, ProductDraftWrapper.class);
        assertThat(productDraftWrapper).isNotNull();

        final ProductDraft productDraft = productDraftWrapper.createProductDraft(productType, taxCategory);
        assertThat(productDraft).isNotNull();
        assertThat(productDraft.getName()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "Classic box"));
        assertThat(productDraft.getProductType().getTypeId()).isEqualTo(productType.toReference().getTypeId());
        assertThat(productDraft.getSlug()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "donut-box"));
        assertThat(productDraft.getMasterVariant().getSku()).isEqualTo("box6");
        assertThat(productDraft.getVariants().isEmpty()).isTrue();
    }
}
