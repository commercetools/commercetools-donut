package utils.pactas;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;
import io.sphere.client.shop.model.Variant;
import org.codehaus.jackson.JsonNode;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;
import utils.Util;

import java.util.List;

public class Invoice extends Pactas {

    public Invoice(String id) {
        if (id != null) {
            authenticate();
            String url = API_URL + "/invoices/" + id;
            try {
                // Send request
                F.Promise<WS.Response> promise = WS.url(url)
                        .setContentType("application/x-www-form-urlencoded")
                        .setQueryParameter("access_token", access_token)
                        .get();

                // Read request
                System.out.println(promise.get().getBody());
                response = Json.parse(promise.get().getBody());
            } catch (Exception e) {
                play.Logger.error("Error on requesting contract");
            }
            checkResponse();
        }
    }

    public Variant getVariant() {
        Variant variant = null;
        if (response.has("ItemList")) {
            List<JsonNode> nodes = response.get("ItemList").findValues("ProductId");
            if (!nodes.isEmpty()) {
                variant = Util.getVariant(nodes.get(0).getTextValue());
            }
        }
        return variant;
    }

    public Address getAddress() {
        Address address = null;
        if (response.has("RecipientAddress")) {
            JsonNode node = response.get("RecipientAddress");
            address = new Address(CountryCode.valueOf(node.get("Country").getTextValue()));
            address.setStreetName(node.get("AddressLine1").getTextValue());
            address.setStreetNumber(node.get("AddressLine2").getTextValue());
            address.setPostalCode(node.get("PostalCode").getTextValue());
            address.setCity(node.get("City").getTextValue());
        }
        return address;
    }
}
