package models.export;

import io.sphere.sdk.producttypes.ProductTypeDraft;
import org.junit.Test;
import utils.JsonUtils;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProductTypeDraftWrapperTest {

    private static final String PRODUCT_TYPE_JSON_RESOURCE = "data/product-type-draft.json";

    @Test
    public void testCreateProductTypeDraft() throws Exception {
        final ProductTypeDraftWrapper productTypeDraftWrapper =
                JsonUtils.readObjectFromResource(PRODUCT_TYPE_JSON_RESOURCE, ProductTypeDraftWrapper.class);
        assertThat(productTypeDraftWrapper).isNotNull();

        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        assertThat(productTypeDraft).isNotNull();
        assertThat(productTypeDraft.getName()).isEqualTo("Donuts box");
        assertThat(productTypeDraft.getDescription()).isEqualTo("A box with delicious donuts");
        assertThat(productTypeDraft.getAttributes().size()).isEqualTo(5);
    }
}
