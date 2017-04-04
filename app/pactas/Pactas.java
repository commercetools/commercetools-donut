package pactas;

import pactas.models.Authorization;
import pactas.models.PactasContract;
import pactas.models.PactasCustomer;

import java.util.concurrent.CompletionStage;

/**
 * Provides an interface to communicate with the Pactas platform.
 */
public interface Pactas {

    /**
     * Returns the promise of the authorization instance for authentication purposes.
     * @return a promise of the authorization.
     */
    CompletionStage<Authorization> fetchAuthorization();

    /**
     * Requests the information of the contract identified with the provided id to Pactas.
     * @param contractId the contract identifier from Pactas.
     * @return a promise of the Pactas contract information identified by this id.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    CompletionStage<PactasContract> fetchContract(String contractId);

    /**
     * Requests the information of the customer identified with the provided id to Pactas.
     * @param customerId the customer identifier from Pactas.
     * @return a promise of the Pactas customer information identified by this id.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    CompletionStage<PactasCustomer> fetchCustomer(String customerId);
}
