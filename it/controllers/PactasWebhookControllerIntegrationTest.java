package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.ConfigFactory;
import io.sphere.client.model.Reference;
import io.sphere.client.model.VersionedId;
import io.sphere.client.shop.model.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import pactas.Pactas;
import pactas.models.Authorization;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Configuration;
import play.api.http.MediaRange;
import play.i18n.Lang;
import play.libs.F;
import play.mvc.Http;
import services.CartServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static utils.JsonUtils.readJsonFromResource;
import static utils.JsonUtils.readObjectFromResource;

public class PactasWebhookControllerIntegrationTest {
    public static final JsonNode WEBHOOK = readJsonFromResource("pactas-webhook-account.json");
    public static final PactasContract CONTRACT = readObjectFromResource("pactas-contract.json", PactasContract.class);
    public static final PactasCustomer CUSTOMER = readObjectFromResource("pactas-customer.json", PactasCustomer.class);

    @Before
    public void setUp() throws Exception {
        final Http.Context emptyContext = new Http.Context(null, null, fakeRequest(),
                Collections.<String, String>emptyMap(),
                Collections.<String, String>emptyMap(),
                Collections.<String, Object>emptyMap());
        Http.Context.current.set(emptyContext);
    }

    @Ignore // requires mocking with SPHERE.IO Play SDK, enable it back with SPHERE.IO JVM SDK
    @Test
    public void testName() throws Exception {
//        final PactasWebhookController controller = new PactasWebhookController(config(), product(), pactas(), orderService);
//        final Result result = controller.createOrderFromSubscription();
//        assertThat(status(result)).isEqualTo(OK);
    }

    private Product product() {
        final Attribute attribute = new Attribute(CartServiceImpl.ID_TWO_WEEKS, CONTRACT.getPlanVariantId());
        return productWithVariant(Variant.create(1, null, null, null, asList(attribute), null));
    }

    private Product productWithVariant(final Variant masterVariant) {
        return new Product(VersionedId.create("id", 0), null, null, null, null, null, null, masterVariant,
                Collections.<Variant>emptyList(), Collections.<Category>emptyList(), Collections.<Reference<Catalog>>emptySet(),
                Reference.<Catalog>create("id", null), ReviewRating.empty(), Reference.<TaxCategory>create("id", null));
    }

    private static Configuration config() {
        return new Configuration(ConfigFactory.load());
    }

    private Pactas pactas() {
        return new Pactas() {
            @Override
            public F.Promise<Authorization> fetchAuthorization() {
                return null;
            }

            @Override
            public F.Promise<PactasContract> fetchContract(final String contractId) {
                return F.Promise.pure(CONTRACT);
            }

            @Override
            public F.Promise<PactasCustomer> fetchCustomer(final String customerId) {
                return F.Promise.pure(CUSTOMER);
            }
        };
    }

    private Http.Request fakeRequest() {
        return new Http.Request() {
            @Override
            public Http.RequestBody body() {
                return new Http.RequestBody() {
                    @Override
                    public String asText() {
                        return WEBHOOK.toString();
                    }
                };
            }

            @Override
            public String uri() {
                return null;
            }

            @Override
            public String method() {
                return null;
            }

            @Override
            public String version() {
                return null;
            }

            @Override
            public String remoteAddress() {
                return null;
            }

            @Override
            public String host() {
                return null;
            }

            @Override
            public String path() {
                return null;
            }

            @Override
            public List<Lang> acceptLanguages() {
                return null;
            }

            @Override
            public List<String> accept() {
                return null;
            }

            @Override
            public List<MediaRange> acceptedTypes() {
                return null;
            }

            @Override
            public boolean accepts(final String mimeType) {
                return false;
            }

            @Override
            public Map<String, String[]> queryString() {
                return null;
            }

            @Override
            public Http.Cookies cookies() {
                return null;
            }

            @Override
            public Map<String, String[]> headers() {
                return null;
            }
        };
    }
}
