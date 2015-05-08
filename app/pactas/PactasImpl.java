package pactas;

import com.ning.http.client.Realm;
import pactas.models.Authorization;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Configuration;
import play.Logger;
import play.libs.F;
import play.libs.WS;
import play.mvc.Http;
import utils.JsonUtils;

public class PactasImpl implements Pactas {
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String ACCESS_TOKEN_PARAMETER = "access_token";
    public static final String AUTH_BODY = "grant_type=client_credentials";

    private final Configuration configuration;
    private final String authUrl;
    private final String clientId;
    private final String clientSecret;
    private final F.Promise<Authorization> authorizationPromise;

    public PactasImpl(final Configuration configuration) {
        this.configuration = configuration;
        this.authUrl = configuration.getString("pactas.auth");
        this.clientId = configuration.getString("pactas.clientId");
        this.clientSecret = configuration.getString("pactas.clientSecret");
        authorizationPromise = authenticate();
    }

    public F.Promise<Authorization> fetchAuthorization() {
        return authorizationPromise;
    }

    @Override
    public F.Promise<PactasContract> fetchContract(final String contractId) {
        String endpointUrl = configuration.getString("pactas.api.contracts") + contractId;
        return executeRequest(endpointUrl, PactasContract.class);
    }

    @Override
    public F.Promise<PactasCustomer> fetchCustomer(final String customerId) {
        String endpointUrl = configuration.getString("pactas.api.customers") + customerId;
        return executeRequest(endpointUrl, PactasCustomer.class);
    }

    /**
     * Sends the request to the provided Pactas endpoint and parses the response.
     * @param endpointUrl endpoint URL to Pactas.
     * @param clazz class against which the response will be deserialized.
     * @return Promise of response deserialized against the given class.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    private <T> F.Promise<T> executeRequest(final String endpointUrl, final Class<T> clazz) {
        return authorizationPromise.flatMap(new F.Function<Authorization, F.Promise<T>>() {
            @Override
            public F.Promise<T> apply(final Authorization authorization) throws Throwable {
                return WS.url(endpointUrl)
                        .setContentType(APPLICATION_FORM_URLENCODED)
                        .setQueryParameter(ACCESS_TOKEN_PARAMETER, authorization.getAccessToken())
                        .get().map(new F.Function<WS.Response, T>() {
                            @Override
                            public T apply(final WS.Response response) throws Throwable {
                                Logger.info(response.getBody());
                                if (response.getStatus() == Http.Status.OK) {
                                    return JsonUtils.readObject(clazz, response.getBody());
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
    private F.Promise<Authorization> authenticate() {
        Logger.debug("Fetching pactas access token");
        return WS.url(authUrl)
                .setContentType(APPLICATION_FORM_URLENCODED)
                .setAuth(clientId, clientSecret, Realm.AuthScheme.BASIC)
                .post(AUTH_BODY).map(new F.Function<WS.Response, Authorization>() {
                    @Override
                    public Authorization apply(final WS.Response response) throws Throwable {
                        Logger.info(response.getBody());
                        if (response.getStatus() == Http.Status.OK) {
                            return JsonUtils.readObject(Authorization.class, response.getBody());
                        } else {
                            throw new PactasException(response.getStatus(), response.getBody());
                        }
                    }
                });
    }
}
