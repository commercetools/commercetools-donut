package services;


import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
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
}
