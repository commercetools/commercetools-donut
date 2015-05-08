package pactas;

import com.typesafe.config.ConfigFactory;
import org.junit.Test;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

public class PactasIntegrationTest {
    private static final Pactas PACTAS = new PactasImpl(config());

    private static final String CUSTOMER_ID = "554b2eb051f45beaec6400b2";
    private static final String CONTRACT_ID = "554b2eb051f45beaec6400b4";

    @Test
    public void fetchesContract() throws Exception {
        final PactasContract contract = PACTAS.fetchContract(CONTRACT_ID).get();
        assertThat(contract.getId()).isEqualTo(CONTRACT_ID);
    }

    @Test
    public void fetchesCustomer() throws Exception {
        final PactasCustomer customer = PACTAS.fetchCustomer(CUSTOMER_ID).get();
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
    }

    private static Configuration config() {
        return new Configuration(ConfigFactory.load());
    }

}
