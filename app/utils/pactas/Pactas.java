package utils.pactas;

import com.fasterxml.jackson.databind.JsonNode;
import play.Play;
import play.mvc.Http;

public class Pactas {

    protected static String API_URL = Play.application().configuration().getString("pactas.api");
    protected com.fasterxml.jackson.databind.JsonNode response;

    public boolean isResponseValid() {
        if (response == null) {
            play.Logger.error("Empty response from Pactas");
            return false;
        }
        if (response.has("message")) {
            play.Logger.error("PACTAS: " + response.get("message").asText());
            return false;
        }
        return true;
    }

    public boolean hasNode(JsonNode node, String name) {
        if (!node.has(name)) {
            play.Logger.debug("Not found attribute " + name);
            return false;
        }
        return true;
    }

    public static String getContractId(Http.Request request) {
        String contractId = null;
        if (request.body().asJson() != null) {
            JsonNode webHook = request.body().asJson();
            if (webHook.get("Event").asText().equals("AccountCreated")) {
                contractId = webHook.get("ContractId").asText();
            }
        }
        return contractId;
    }
}
