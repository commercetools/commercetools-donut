package utils.pactas;

import io.sphere.client.shop.model.Variant;
import org.codehaus.jackson.JsonNode;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import utils.Util;

import java.util.List;

public class Contract extends Pactas {

    public Contract(String id) {
        if (id != null) {
            authenticate();
            String url = API_URL + "/contracts/" + id;
            try {
                // Send request
                F.Promise<WS.Response> promise = WS.url(url)
                        .setContentType("application/x-www-form-urlencoded")
                        .setQueryParameter("access_token", access_token)
                        .get();

                // Read request
                response = Json.parse(promise.get().getBody());
            } catch (Exception e) {
                play.Logger.error("Error on requesting invoice");
            }
            checkResponse();
        }
    }

    public Variant getVariant() {
        Variant variant = null;
        if (response.has("Phases")) {
            List<JsonNode> nodes = response.get("Phases").findValues("PlanVariantId");
            if (!nodes.isEmpty()) {
                variant = Util.getVariant(nodes.get(0).getTextValue());
            }
        }
        return variant;
    }

    public String getCustomerId() {
        if (response.has("CustomerId")) {
            return response.get("CustomerId").getTextValue();
        }
        return null;
    }

}
