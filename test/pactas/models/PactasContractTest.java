package pactas.models;

import org.junit.Test;

import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.PactasJsonUtils.readObjectFromResource;

public class PactasContractTest {

    private static final PactasContract CONTRACT = readObjectFromResource("pactas-contract.json", PactasContract.class);
    private static final Currency EUR = Currency.getInstance("EUR");

    @Test
    public void parsesContractInformation() throws Exception {
        assertThat(CONTRACT.getId()).isEqualTo("554b2eb051f45beaec6400b4");
        assertThat(CONTRACT.getCustomerId()).isEqualTo("554b2eb051f45beaec6400b2");
        assertThat(CONTRACT.getPlanId()).isEqualTo("53284bc151f459b0d07df616");
        assertThat(CONTRACT.getPlanVariantId()).isEqualTo("53284c7751f459b0d07df61a");
        assertThat(CONTRACT.getCurrency()).isEqualTo("EUR");
    }

    @Test
    public void convertsToCurrency() throws Exception {
        assertThat(CONTRACT.getMonetaryCurrency()).isEqualTo(EUR);
    }
}
