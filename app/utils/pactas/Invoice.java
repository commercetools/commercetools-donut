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

    private Invoice(String id) {
        if (id != null) {
            String url = API_URL + "/invoices/" + id;
            try {
                // Send request
                F.Promise<WS.Response> promise = WS.url(url)
                        .setContentType("application/x-www-form-urlencoded")
                        .setQueryParameter("access_token", Authorization.access_token)
                        .get();

                // Read request
                response = Json.parse(promise.get().getBody());
            } catch (Exception e) {
                play.Logger.error("Error on requesting contract");
            }
        }
    }

    public static Invoice get(String id) {
        Invoice invoice = new Invoice(id);
        if (invoice.isResponseValid()) return invoice;
        return null;
    }

    public Variant getVariant() {
        Variant variant = null;
        if (response != null && hasNode(response, "ItemList")) {
            List<JsonNode> nodes = response.get("ItemList").findValues("ProductId");
            if (!nodes.isEmpty()) {
                variant = Util.getVariant(nodes.get(0).getTextValue());
            }
        }
        return variant;
    }

    public Address getAddress() {
        Address address = null;
        if (response != null && hasNode(response, "RecipientAddress")) {
            JsonNode node = response.get("RecipientAddress");
            if (!hasNode(node, "Country")) return null;
            address = new Address(CountryCode.valueOf(node.get("Country").getTextValue()));
            if (hasNode(node, "AddressLine1")) address.setStreetName(node.get("AddressLine1").getTextValue());
            if (hasNode(node, "AddressLine2")) address.setStreetNumber(node.get("AddressLine2").getTextValue());
            if (hasNode(node, "PostalCode")) address.setPostalCode(node.get("PostalCode").getTextValue());
            if (hasNode(node, "City")) address.setCity(node.get("City").getTextValue());
        }
        return address;
    }
}
