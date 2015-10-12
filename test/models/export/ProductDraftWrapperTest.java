package models.export;

import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.ProductDraft;
import io.sphere.sdk.producttypes.ProductType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import utils.JsonUtils;

import java.util.UUID;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Mockito.when;

public class ProductDraftWrapperTest {

    private static final String PRODUCT_JSON_RESOURCE = "data/product-draft.json";

    private ProductType productType;

    @Before
    public void setUp() {
        productType = Mockito.mock(ProductType.class);
        final String productTypeId = UUID.randomUUID().toString();
        when(productType.toReference()).thenReturn(Reference.of("product-type", productTypeId, productType));
        when(productType.getId()).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    public void testCreateProductDraft() throws Exception {
        final ProductDraftWrapper productDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_JSON_RESOURCE, ProductDraftWrapper.class);
        assertThat(productDraftWrapper).isNotNull();

        final ProductDraft productDraft = productDraftWrapper.createProductDraft(productType);
        System.err.println(productDraft);
        assertThat(productDraft).isNotNull();
        assertThat(productDraft.getName()).isEqualTo(productDraftWrapper.getName());
        assertThat(productDraft.getProductType().getTypeId()).isEqualTo(productType.toReference().getTypeId());
        assertThat(productDraft.getSlug()).isEqualTo(productDraftWrapper.getSlug());
        assertThat(productDraft.getMasterVariant().getSku()).isEqualTo(productDraftWrapper.getMasterVariant().getSku());
        assertThat(productDraft.getVariants().size()).isEqualTo(0);
    }
}
