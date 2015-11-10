package services;

import com.google.common.collect.ImmutableMap;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.Configuration;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;
import play.test.WithApplication;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class ImportServiceIntegrationTest extends WithApplication {


    private Application application;
    private ImportService importService;
    private PlayJavaSphereClient playJavaSphereClient;

    @Override
    protected Application provideApplication() {
        final Configuration config = new Configuration(ImmutableMap.of("fixtures.import.enabled", "true"));
        application = new GuiceApplicationBuilder().configure(config).build();
        return application;
    }

    @Before
    public void setUp() {
        importService = application.injector().instanceOf(ImportService.class);
        playJavaSphereClient = application.injector().instanceOf(PlayJavaSphereClient.class);
    }

    @Test
    public void testImportProductModel() {
        //import is triggered in service constructor, so just checking for product existence
        final F.Promise<PagedQueryResult<ProductProjection>> resultPromise =
                playJavaSphereClient.execute(ProductProjectionQuery.ofCurrent());
        final Optional<ProductProjection> result = resultPromise.get(5000, TimeUnit.MILLISECONDS).head();
        assertThat(result).isPresent();
        assertThat(result.get().getProductType()).isNotNull();
        assertThat(result.get().getTaxCategory()).isNotNull();
        assertThat(result.get().getAllVariants().size()).isEqualTo(4);
    }
}
