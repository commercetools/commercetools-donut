package pactas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authorization {
    private final String accessToken;
    private final String expires;
    private final String tokenType;

    private Authorization(@JsonProperty("access_token") String accessToken,
                         @JsonProperty("expires") String expires,
                         @JsonProperty("token_type") String tokenType) {
        this.accessToken = accessToken;
        this.expires = expires;
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getExpires() {
        return expires;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "Authorization{" +
                "accessToken='" + accessToken + '\'' +
                ", expires='" + expires + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
