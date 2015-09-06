package pactas.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;

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
        AddressBuilder addressBuilder = AddressBuilder.of(getCountryCode());
        if (addressLine != null) {
            addressBuilder.streetName(addressLine);
        }
        if (postalCode != null) {
            addressBuilder.postalCode(postalCode);
        }
        if (city != null) {
            addressBuilder.city(city);
        }
        return addressBuilder.build();
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
