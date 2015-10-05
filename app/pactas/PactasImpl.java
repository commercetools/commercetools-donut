package pactas;

import com.google.common.net.HttpHeaders;
import pactas.models.Authorization;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Configuration;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import play.libs.ws.WSAuthScheme;
import play.mvc.Http;
import utils.JsonUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PactasImpl implements Pactas {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Configuration configuration;
    private final String authUrl;
    private final String clientId;
    private final String clientSecret;
    private final F.Promise<Authorization> authorizationPromise;

    @Inject
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
        return authorizationPromise.flatMap(authorization -> WS.url(endpointUrl)
                .setContentType(CONTENT_TYPE)
                .setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authorization.getAccessToken())
                .get().map(response -> {
                    Logger.info(response.getBody());
                    if (response.getStatus() == Http.Status.OK) {
                        return JsonUtils.readObject(clazz, response.getBody());
                    } else {
                        throw new PactasException(response.getStatus(), response.getBody());
                    }
                }));
    }

    /**
     * Sends the request to the auth Pactas endpoint and parses the access token.
     * @return Promise of the access token.
     */
    private F.Promise<Authorization> authenticate() {
        Logger.debug("Fetching pactas access token");
        return WS.url(authUrl)
                .setContentType(CONTENT_TYPE)
                .setAuth(clientId, clientSecret, WSAuthScheme.BASIC)
                .post("grant_type=client_credentials").map(response -> {
                    Logger.info(response.getBody());
                    if (response.getStatus() == Http.Status.OK) {
                        return JsonUtils.readObject(Authorization.class, response.getBody());
                    } else {
                        throw new PactasException(response.getStatus(), response.getBody());
                    }
                });
    }
}
