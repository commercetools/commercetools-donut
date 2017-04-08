package pactas.models;

import io.sphere.sdk.models.Address;
import org.junit.Test;

import static com.neovisionaries.i18n.CountryCode.DE;
import static org.assertj.core.api.Assertions.assertThat;
import static pactas.PactasJsonUtils.readObjectFromResource;

public class PactasCustomerTest {

    private static final PactasCustomer CUSTOMER = readObjectFromResource("pactas-customer.json", PactasCustomer.class);

    @Test
    public void parsesCustomerInformation() throws Exception {
        assertThat(CUSTOMER.getId()).isEqualTo("554b2eb051f45beaec6400b2");
        assertThat(CUSTOMER.getFirstName()).isEqualTo("Clancy");
        assertThat(CUSTOMER.getLastName()).isEqualTo("Wiggum");
    }

    @Test
    public void parsesCustomerAddressInformation() throws Exception {
        final PostalAddress address = CUSTOMER.getAddress();
        assertThat(address.getAddressLine()).isEqualTo("Elm Street, 792");
        assertThat(address.getPostalCode()).isEqualTo("49007");
        assertThat(address.getCity()).isEqualTo("Springfield");
        assertThat(address.getCountry()).isEqualTo("DE");
    }

    @Test
    public void convertsToCountryCode() throws Exception {
        assertThat(CUSTOMER.getAddress().getCountryCode()).isEqualTo(DE);
    }

    @Test
    public void convertsToAddress() throws Exception {
        final Address address = CUSTOMER.getCompleteAddress();
        assertThat(address.getFirstName()).isEqualTo("Clancy");
        assertThat(address.getLastName()).isEqualTo("Wiggum");
        assertThat(address.getStreetName()).isEqualTo("Elm Street, 792");
        assertThat(address.getPostalCode()).isEqualTo("49007");
        assertThat(address.getCity()).isEqualTo("Springfield");
        assertThat(address.getCountry()).isEqualTo(DE);
    }
}
