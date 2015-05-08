package pactas.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.client.shop.model.Address;

public class PostalAddress {
    private String addressLine;
    private String postalCode;
    private String city;
    private String country;

    private PostalAddress(@JsonProperty("AddressLine1") String addressLine,
                          @JsonProperty("PostalCode") String postalCode,
                          @JsonProperty("City") String city,
                          @JsonProperty("Country") String country) {
        this.addressLine = addressLine;
        this.postalCode = postalCode;
        this.city = city;
        this.country = country;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public CountryCode getCountryCode() {
        return CountryCode.valueOf(country);
    }

    public Address getAddress() {
        Address address = new Address(getCountryCode());
        if (addressLine != null) {
            address.setStreetName(addressLine);
        }
        if (postalCode != null) {
            address.setPostalCode(postalCode);
        }
        if (city != null) {
            address.setCity(city);
        }
        return address;
    }

    @Override
    public String toString() {
        return "PostalAddress{" +
                "addressLine='" + addressLine + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
