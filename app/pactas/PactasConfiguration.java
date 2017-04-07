package pactas;

import play.Configuration;

import javax.inject.Inject;
import java.util.Optional;

public class PactasConfiguration {

    private static final String DEFAULT_API_URL = "https://sandbox.billwerk.com/api/v1";
    private static final String DEFAULT_AUTH_URL = "https://sandbox.billwerk.com/oauth/token";

    private final String authUrl;
    private final String apiUrl;
    private final String publicKey;
    private final String clientId;
    private final String clientSecret;

    @Inject
    public PactasConfiguration(final Configuration configuration) {
        this.authUrl = configuration.getString("pactas.auth", DEFAULT_AUTH_URL);
        this.apiUrl = configuration.getString("pactas.api", DEFAULT_API_URL);
        this.publicKey = Optional.ofNullable(configuration.getString("pactas.publicKey"))
                .orElseThrow(() -> new IllegalArgumentException("Missing Pactas public key"));
        this.clientId = Optional.ofNullable(configuration.getString("pactas.clientId"))
                .orElseThrow(() ->  new IllegalArgumentException("Missing Pactas client ID"));
        this.clientSecret = Optional.ofNullable(configuration.getString("pactas.clientSecret"))
                .orElseThrow(() -> new IllegalArgumentException("Missing Pactas client secret"));
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
