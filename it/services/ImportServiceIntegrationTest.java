package services;


import io.sphere.sdk.products.Product;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ImportServiceIntegrationTest {

    private Application application;
    private ImportService importService;

    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().build();
        importService = application.injector().instanceOf(ImportService.class);
    }

    @Test
    public void testExportCustomType() {
        //TODO
    }

    @Test
    public void testExportProductModel() {
        final F.Promise<Product> productPromise = importService.exportProductModel();
        final Product product = productPromise.get(5000);
        assertThat(product).isNotNull();
    }
}
