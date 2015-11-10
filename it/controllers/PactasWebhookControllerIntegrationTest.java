package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import pactas.PactasException;
import play.Application;
import play.api.http.MediaRange;
import play.api.mvc.Request;
import play.api.mvc.RequestHeader;
import play.i18n.Lang;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static play.mvc.Http.Status.OK;
import static utils.JsonUtils.readJsonFromResource;

public class PactasWebhookControllerIntegrationTest extends WithApplication {

    public static final JsonNode WEBHOOK = readJsonFromResource("pactas-webhook-account.json");

    private static final long ALLOWED_TIMEOUT = 5000;

    private Application application;

    @Override
    protected Application provideApplication() {
        application = new GuiceApplicationBuilder().build();
        return application;
    }

    @Before
    public void setUp() throws Exception {
        final Http.Context emptyContext = new Http.Context(null, null, fakeRequest(),
                Collections.<String, String>emptyMap(),
                Collections.<String, String>emptyMap(),
                Collections.<String, Object>emptyMap());
        Http.Context.current.set(emptyContext);
    }

    @Test(expected = PactasException.class)
    public void testCreateOrderFromSubscription() throws Exception {
        final PactasWebhookController controller =  application.injector().instanceOf(PactasWebhookController.class);
        final Result result = controller.createOrderFromSubscription().get(ALLOWED_TIMEOUT, TimeUnit.MILLISECONDS);
        assertThat(result.status()).isEqualTo(OK);
        assertThat(result).isEqualTo(OK);
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
            public String username() {
                return null;
            }

            @Override
            public void setUsername(String username) {

            }

            @Override
            public Http.Request withUsername(String username) {
                return null;
            }

            @Override
            public Request<Http.RequestBody> _underlyingRequest() {
                return null;
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
            public boolean secure() {
                return false;
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
            public String getQueryString(String key) {
                return null;
            }

            @Override
            public Http.Cookies cookies() {
                return null;
            }

            @Override
            public Http.Cookie cookie(String name) {
                return null;
            }

            @Override
            public Map<String, String[]> headers() {
                return null;
            }

            @Override
            public String getHeader(String headerName) {
                return null;
            }

            @Override
            public boolean hasHeader(String headerName) {
                return false;
            }

            @Override
            public RequestHeader _underlyingHeader() {
                return null;
            }
        };
    }
}
