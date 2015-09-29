package pactas;

import org.junit.Test;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class PactasIntegrationTest extends WithApplication {

    private static final String CUSTOMER_ID = "554b2eb051f45beaec6400b2";
    private static final String CONTRACT_ID = "554b2eb051f45beaec6400b4";

    private static final long ALLOWED_TIMEOUT = 3000;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    //FIX ME {"error":"invalid_client","error_description":"unknown client"}
    @Test(expected = PactasException.class)
    public void fetchesContract() throws Exception {
        final Pactas pactas = app.injector().instanceOf(Pactas.class);
        final PactasContract contract = pactas.fetchContract(CONTRACT_ID).get(ALLOWED_TIMEOUT, TimeUnit.MILLISECONDS);
        assertThat(contract.getId()).isEqualTo(CONTRACT_ID);
    }

    //FIX ME {"error":"invalid_client","error_description":"unknown client"}
    @Test(expected = PactasException.class)
    public void fetchesCustomer() throws Exception {
        final Pactas pactas = app.injector().instanceOf(Pactas.class);
        final PactasCustomer customer = pactas.fetchCustomer(CUSTOMER_ID).get(ALLOWED_TIMEOUT, TimeUnit.MILLISECONDS);
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
    }


}
