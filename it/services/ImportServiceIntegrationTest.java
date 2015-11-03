package services;


import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartDeleteCommand;
import io.sphere.sdk.carts.queries.CartQuery;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.Product;
import io.sphere.sdk.products.commands.ProductDeleteCommand;
import io.sphere.sdk.products.queries.ProductQuery;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.commands.ProductTypeDeleteCommand;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import io.sphere.sdk.taxcategories.TaxCategory;
import io.sphere.sdk.taxcategories.commands.TaxCategoryDeleteCommand;
import io.sphere.sdk.taxcategories.queries.TaxCategoryQuery;
import io.sphere.sdk.types.FieldDefinition;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.commands.TypeDeleteCommand;
import io.sphere.sdk.types.queries.TypeQuery;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static play.test.Helpers.running;

public class ImportServiceIntegrationTest {

    private static final int ALLOWED_TIMEOUT = 5000;
    private Application application;
    private ImportService importService;
    private PlayJavaSphereClient sphereClient;

    @Before
    public void setUp() {
        application = new GuiceApplicationBuilder().build();
        importService = application.injector().instanceOf(ImportService.class);
        sphereClient = application.injector().instanceOf(PlayJavaSphereClient.class);
    }

    @Test
    public void testExportProductModel() {
        //TODO
    }

    //@Test
    public void testExportCustomType() {
        running(application, () -> {
            final Type customType = importService.exportCustomType().get(ALLOWED_TIMEOUT);
            assertThat(customType).isNotNull();
            assertThat(customType.getKey()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "cart-frequency-key"));
            assertThat(customType.getName()).isEqualTo("custom type for delivery frequency");
            assertThat(customType.getFieldDefinitions().size()).isEqualTo(1);
            final FieldDefinition fieldDefinition = customType.getFieldDefinitions().get(0);
            assertThat(fieldDefinition.getName()).isEqualTo("frequency");
            assertThat(fieldDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "selected frequency"));
        });
    }

    //@Test
    public void getCustomTypes() {
        running(application, () -> {
            final TypeQuery query = TypeQuery.of();
            final List<Type> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            final Optional<Type> optionalType = results.stream().filter(type -> "cart-frequency-key".equals(type.getKey())).findFirst();
            assertThat(optionalType.isPresent()).isTrue();
        });
    }

    //@Test
    public void deleteCustomType() {
        running(application, () -> {
            final TypeQuery query = TypeQuery.of();
            final List<Type> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(type -> {
                final Type execute = sphereClient.execute(TypeDeleteCommand.of(type)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + type.getKey());
            });
        });
    }

    //@Test
    public void deleteCarts() {
        running(application, () -> {
            final CartQuery query = CartQuery.of();
            final List<Cart> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(cart -> {
                final Cart execute = sphereClient.execute(CartDeleteCommand.of(cart)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + execute);
            });
        });
    }

    //@Test
    public void deleteProducts() {
        running(application, () -> {
            final ProductQuery query = ProductQuery.of();
            final List<Product> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(product -> {
                final Product execute = sphereClient.execute(ProductDeleteCommand.of(product)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + execute.getId());
            });
        });
    }

    //@Test
    private void deleteProductTypes() {
        running(application, () -> {
            final ProductTypeQuery query = ProductTypeQuery.of();
            final List<ProductType> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(productType -> {
                final ProductType execute = sphereClient.execute(ProductTypeDeleteCommand.of(productType)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + execute.getId());
            });
        });
    }


    //@Test
    public void deleteTaxCategory() {
        running(application, () -> {
            final TaxCategoryQuery query = TaxCategoryQuery.of();
            final List<TaxCategory> results = sphereClient.execute(query).get(ALLOWED_TIMEOUT).getResults();
            results.forEach(taxCategory -> {
                final TaxCategory execute = sphereClient.execute(TaxCategoryDeleteCommand.of(taxCategory)).get(ALLOWED_TIMEOUT);
                System.err.println("####" + execute.getId());
            });
        });
    }
}
