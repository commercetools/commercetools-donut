package pactas;

import org.junit.Before;
import org.junit.Test;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import play.test.WithServer;

import static org.assertj.core.api.Assertions.assertThat;

public class PactasIntegrationTest extends WithApplication {

    private static final String CUSTOMER_ID = "58e3a4af14aa010f3864eda0";
    private static final String CONTRACT_ID = "58e3a4af14aa010f3864eda1";

    private Pactas pactas;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void setUp() throws Exception {
        this.pactas = app.injector().instanceOf(Pactas.class);
    }

    @Test
    public void fetchesContract() throws Exception {
        final PactasContract contract = pactas.fetchContract(CONTRACT_ID).toCompletableFuture().get();
        assertThat(contract.getId()).isEqualTo(CONTRACT_ID);
    }

    @Test
    public void fetchesCustomer() throws Exception {
        final PactasCustomer customer = pactas.fetchCustomer(CUSTOMER_ID).toCompletableFuture().get();
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
    }
}
