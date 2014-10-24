package pactas;

import pactas.models.PactasContract;
import pactas.models.PactasCustomer;
import pactas.models.PactasInvoice;
import play.libs.F;

/**
 * Provides an interface to communicate with the Pactas platform.
 */
public interface Pactas {

    /**
     * Requests the information of the contract identified with the provided id to Pactas.
     * @param contractId the contract identifier from Pactas.
     * @return a promise of the Pactas contract information identified by this id.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    F.Promise<PactasContract> contract(String contractId);

    /**
     * Requests the information of the invoice identified with the provided id to Pactas.
     * @param invoiceId the invoice identifier from Pactas.
     * @return a promise of the Pactas invoice information identified by this id.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    F.Promise<PactasInvoice> invoice(String invoiceId);

    /**
     * Requests the information of the customer identified with the provided id to Pactas.
     * @param customerId the customer identifier from Pactas.
     * @return a promise of the Pactas customer information identified by this id.
     * @throws PactasException when the request failed or the response could not be parsed.
     */
    F.Promise<PactasCustomer> customer(String customerId);
}
