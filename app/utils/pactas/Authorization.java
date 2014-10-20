package utils.pactas;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.Realm;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

public class Authorization {
    private final static String AUTH_URL = Play.application().configuration().getString("pactas.auth");
    private final static String CLIENT_ID = Play.application().configuration().getString("pactas.clientId");
    private final static String CLIENT_SECRET = Play.application().configuration().getString("pactas.clientSecret");

    final protected static String access_token = authenticate();
    protected JsonNode response;

    public static String authenticate() {
        try {
            // Send request
            play.Logger.debug("Sending auth request to " + AUTH_URL);
            F.Promise<WS.Response> promise = WS.url(AUTH_URL)
                    .setContentType("application/x-www-form-urlencoded")
                    .setAuth(CLIENT_ID, CLIENT_SECRET, Realm.AuthScheme.BASIC)
                    .post("grant_type=client_credentials");

            // Read request
            JsonNode res = Json.parse(promise.get().getBody());
            play.Logger.debug("Auth response received from Pactas");
            if (res.has("access_token")) {
                play.Logger.debug("Received access token");
                return res.get("access_token").asText();
            }
            play.Logger.error("Error on authentication: "+ Json.stringify(res));
        } catch (Exception e) {
            play.Logger.error("Error on authentication: " + e.getMessage());
        }
        return "";
    }
}