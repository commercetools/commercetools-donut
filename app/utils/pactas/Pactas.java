package utils.pactas;

import com.ning.http.client.Realm;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

public class Pactas {

    protected static String API_URL = Play.application().configuration().getString("pactas.api");
    private static String AUTH_URL = Play.application().configuration().getString("pactas.auth");
    private static String CLIENT_ID = Play.application().configuration().getString("pactas.clientId");
    private static String CLIENT_SECRET = Play.application().configuration().getString("pactas.clientSecret");

    protected String access_token;
    protected JsonNode response;

    public void authenticate() {
        if (access_token != null) return;
        try {
            ObjectNode body = Json.newObject();
            body.put("grant_type", "client_credentials");
            // Send request
            play.Logger.debug("Sending auth request to " + AUTH_URL);
            play.Logger.debug("With credentials " + CLIENT_ID + ":" + CLIENT_SECRET);
            F.Promise<WS.Response> promise = WS.url(AUTH_URL)
                    .setContentType("application/x-www-form-urlencoded")
                    .setAuth(CLIENT_ID, CLIENT_SECRET, Realm.AuthScheme.BASIC)
                    .post(body);

            // Read request
            play.Logger.debug("Auth response received from Pactas");
            JsonNode res = Json.parse(promise.get().getBody());
            access_token = res.get("access_token").getTextValue();
            play.Logger.debug("Received access token " + access_token);
        } catch (Exception e) {
            play.Logger.error("Error on authentication");
        }
    }

    public boolean isResponseValid() {
        if (response == null) {
            play.Logger.error("Empty response from Pactas");
            return false;
        }
        if (response.has("message")) {
            play.Logger.error("PACTAS: " + response.get("message").getTextValue());
            return false;
        }
        return true;
    }

    public boolean hasNode(JsonNode node, String name) {
        if (!node.has(name)) {
            play.Logger.error("Not found attribute " + name);
            return false;
        }
        return true;
    }
}
