package utils.pactas;

import com.fasterxml.jackson.databind.JsonNode;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

public class Customer extends Pactas {

    private Customer(String id) {
        if (id != null) {
            String url = API_URL + "/customers/" + id;
            try {
                // Send request
                F.Promise<WS.Response> promise = WS.url(url)
                        .setContentType("application/x-www-form-urlencoded")
                        .setQueryParameter("access_token", Authorization.access_token)
                        .get();

                // Read request
                response = Json.parse(promise.get().getBody());
            } catch (Exception e) {
                play.Logger.error("Error on requesting customer: " + e.getMessage());
            }
        }
    }

    public static Customer get(String id) {
        Customer customer = new Customer(id);
        if (customer.isResponseValid()) return customer;
        return null;
    }

    public Address getAddress() {
        Address address = null;
        if (response != null && hasNode(response, "Address")) {
            JsonNode node = response.get("Address");
            if (!hasNode(node, "Country")) return null;
            address = new Address(CountryCode.valueOf(node.get("Country").asText()));
            if (hasNode(response, "FirstName")) address.setFirstName(response.get("FirstName").asText());
            if (hasNode(response, "LastName")) address.setLastName(response.get("LastName").asText());
            if (hasNode(node, "AddressLine1")) address.setStreetName(node.get("AddressLine1").asText());
            if (hasNode(node, "AddressLine2")) address.setStreetNumber(node.get("AddressLine2").asText());
            if (hasNode(node, "PostalCode")) address.setPostalCode(node.get("PostalCode").asText());
            if (hasNode(node, "City")) address.setCity(node.get("City").asText());
        }
        return address;
    }

}
