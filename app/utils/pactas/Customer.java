package utils.pactas;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;
import org.codehaus.jackson.JsonNode;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

public class Customer extends Pactas {

    private Customer(String id) {
        if (id != null) {
            authenticate();
            String url = API_URL + "/customers/" + id;
            try {
                // Send request
                F.Promise<WS.Response> promise = WS.url(url)
                        .setContentType("application/x-www-form-urlencoded")
                        .setQueryParameter("access_token", access_token)
                        .get();

                // Read request
                response = Json.parse(promise.get().getBody());
            } catch (Exception e) {
                play.Logger.error("Error on requesting customer");
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
            address = new Address(CountryCode.valueOf(node.get("Country").getTextValue()));
            if (hasNode(response, "FirstName")) address.setFirstName(response.get("FirstName").getTextValue());
            if (hasNode(response, "LastName")) address.setLastName(response.get("LastName").getTextValue());
            if (hasNode(node, "AddressLine1")) address.setStreetName(node.get("AddressLine1").getTextValue());
            if (hasNode(node, "AddressLine2")) address.setStreetNumber(node.get("AddressLine2").getTextValue());
            if (hasNode(node, "PostalCode")) address.setPostalCode(node.get("PostalCode").getTextValue());
            if (hasNode(node, "City")) address.setCity(node.get("City").getTextValue());
        }
        return address;
    }

}
