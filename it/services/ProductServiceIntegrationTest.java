package services;

import io.sphere.sdk.products.ProductProjection;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;

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

    @Test
    public void _testLoadProduct() throws Exception {
        final Application app = new GuiceApplicationBuilder().build();
        final ProductService productService = app.injector().instanceOf(ProductService.class);
        final F.Promise<Optional<ProductProjection>> result = productService._getProduct();
        assertThat(result.get(2000).isPresent()).isTrue();
    }
}