package models.export;

import io.sphere.sdk.producttypes.ProductTypeDraft;
import org.junit.Test;
import utils.JsonUtils;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ProductTypeDraftWrapperTest {

    @Test
    public void testCreateProductTypeDraft() throws Exception {
        final ProductTypeDraftWrapper productTypeDraftWrapper =
                JsonUtils.readObjectFromResource("data/product-type-draft.json", ProductTypeDraftWrapper.class);
        assertThat(productTypeDraftWrapper).isNotNull();

        final ProductTypeDraft productTypeDraft = productTypeDraftWrapper.createProductTypeDraft();
        assertThat(productTypeDraft).isNotNull();
        assertThat(productTypeDraft.getName()).isEqualTo("Donuts box");
        assertThat(productTypeDraft.getDescription()).isEqualTo("A box with delicious donuts");
        assertThat(productTypeDraft.getAttributes().size()).isEqualTo(5);
    }
}
