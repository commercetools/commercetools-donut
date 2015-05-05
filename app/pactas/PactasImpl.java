package pactas;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.Realm;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.PactasInvoice;
import play.Configuration;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import play.mvc.Http;

public class PactasImpl implements Pactas {
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String ACCESS_TOKEN_PARAMETER = "access_token";
    public static final String AUTH_BODY = "grant_type=client_credentials";
    public static final String ACCESS_TOKEN_NODE = "access_token";

    private final Configuration configuration;

    private final String authUrl;
    private final String clientId;
    private final String clientSecret;

    public PactasImpl(final Configuration configuration) {
        this.configuration = configuration;
        this.authUrl = configuration.getString("pactas.auth");
        this.clientId = configuration.getString("pactas.clientId");
        this.clientSecret = configuration.getString("pactas.clientSecret");
    }

    @Override
    public F.Promise<PactasContract> contract(final String contractId) {
        String endpointUrl = configuration.getString("pactas.api.contracts") + contractId;
        return get(endpointUrl).map(new F.Function<JsonNode, PactasContract>() {
            @Override
            public PactasContract apply(JsonNode jsonResponse) throws Throwable {
                try {
                    return Json.fromJson(jsonResponse, PactasContract.class);
                } catch (Exception e) {
                    throw new PactasException(e);
                }
            }
        });
    }

    @Override
    public F.Promise<PactasInvoice> invoice(final String invoiceId) {
        String endpointUrl = configuration.getString("pactas.api.invoices") + invoiceId;
        return get(endpointUrl).map(new F.Function<JsonNode, PactasInvoice>() {
            @Override
            public PactasInvoice apply(JsonNode jsonResponse) throws Throwable {
                try {
                    return Json.fromJson(jsonResponse, PactasInvoice.class);
                } catch (Exception e) {
                    throw new PactasException(e);
                }
            }
        });
    }

    @Override
    public F.Promise<PactasCustomer> customer(final String customerId) {
        String endpointUrl = configuration.getString("pactas.api.customers") + customerId;
        return get(endpointUrl).map(new F.Function<JsonNode, PactasCustomer>() {
            @Override
            public PactasCustomer apply(JsonNode jsonResponse) throws Throwable {
                try {
                    return Json.fromJson(jsonResponse, PactasCustomer.class);
                } catch (Exception e) {
                    throw new PactasException(e);
                }
            }
        });
    }

    /**
     * Sends the request to the provided Pactas endpoint and parses the response.
     * @param endpointUrl endpoint URL to Pactas.
     * @return Promise of Json response.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    private F.Promise<JsonNode> get(final String endpointUrl) {
        return authenticate().flatMap(new F.Function<String, F.Promise<JsonNode>>() {
            @Override
            public F.Promise<JsonNode> apply(final String accessToken) throws Throwable {
                return WS.url(endpointUrl)
                        .setContentType(APPLICATION_FORM_URLENCODED)
                        .setQueryParameter(ACCESS_TOKEN_PARAMETER, accessToken)
                        .get().map(new F.Function<WS.Response, JsonNode>() {
                            @Override
                            public JsonNode apply(final WS.Response response) throws Throwable {
                                Logger.debug(response.getBody());
                                if (response.getStatus() == Http.Status.OK) {
                                    return response.asJson();
                                } else {
                                    throw new PactasException(response.getStatus(), response.getBody());
                                }
                            }
                        });

            }
        });
    }

    /**
     * Sends the request to the auth Pactas endpoint and parses the access token.
     * @return Promise of the access token.
     */
    private F.Promise<String> authenticate() {
        Logger.debug("Fetching pactas access token");
        return WS.url(authUrl)
                .setContentType(APPLICATION_FORM_URLENCODED)
                .setAuth(clientId, clientSecret, Realm.AuthScheme.BASIC)
                .post(AUTH_BODY).map(new F.Function<WS.Response, String>() {
                    @Override
                    public String apply(final WS.Response response) throws Throwable {
                        Logger.debug(response.getBody());
                        if (response.getStatus() == Http.Status.OK) {
                            JsonNode responseNode = response.asJson();
                            if (responseNode.has(ACCESS_TOKEN_NODE)) {
                                return responseNode.get(ACCESS_TOKEN_NODE).asText();
                            }
                        }
                        throw new PactasException(response.getStatus(), response.getBody());
                    }
                });
    }
}
