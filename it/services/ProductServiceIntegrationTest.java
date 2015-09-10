package services;

import io.sphere.sdk.products.ProductProjection;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductServiceIntegrationTest {

    @Test
    public void testLoadProduct() throws Exception {
        final Application app = new GuiceApplicationBuilder().build();
        final ProductService productService = app.injector().instanceOf(ProductService.class);
        final Optional<ProductProjection> result = productService.getProduct();
        assertThat(result.isPresent()).isTrue();
    }
}
