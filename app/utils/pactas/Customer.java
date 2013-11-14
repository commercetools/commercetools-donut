package utils.pactas;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;
import org.codehaus.jackson.JsonNode;
import play.libs.F;
import play.libs.Json;
import play.libs.WS;

public class Customer extends Pactas {

    public Customer(String id) {
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
            checkResponse();
        }
    }

    public Address getAddress() {
        Address address = null;
        if (response.has("Address")) {
            JsonNode node = response.get("Address");
            address = new Address(CountryCode.valueOf(node.get("Country").getTextValue()));
            address.setStreetName(node.get("AddressLine1").getTextValue());
            address.setStreetNumber(node.get("AddressLine2").getTextValue());
            address.setPostalCode(node.get("PostalCode").getTextValue());
            address.setCity(node.get("City").getTextValue());
            address.setFirstName(response.get("FirstName").getTextValue());
            address.setLastName(response.get("LastName").getTextValue());
        }
        return address;
    }

}
