package utils.pactas;

import com.ning.http.client.Realm;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

import java.util.logging.Logger;

public class Authorization {
    private static String AUTH_URL = Play.application().configuration().getString("pactas.auth");
    private static String CLIENT_ID = Play.application().configuration().getString("pactas.clientId");
    private static String CLIENT_SECRET = Play.application().configuration().getString("pactas.clientSecret");

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
            play.Logger.debug("Auth response received from Pactas");
            JsonNode res = Json.parse(promise.get().getBody());
            if (res.has("access_token")) {
                play.Logger.debug("Received access token");
                return res.get("access_token").getTextValue();
            }
            play.Logger.error("Error on authentication "+ Json.stringify(res));
        } catch (Exception e) {
            play.Logger.error("Error on authentication");
        }
        return "";
    }
}