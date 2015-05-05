package pactas.models.webhooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPaymentSucceeded implements Webhook {
    private final String paymentTransactionId;

    private WebhookPaymentSucceeded(@JsonProperty("PaymentTransactionId") String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    @Override
    public String toString() {
        return "WebhookPaymentSucceeded{" +
                "paymentTransactionId='" + paymentTransactionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebhookPaymentSucceeded that = (WebhookPaymentSucceeded) o;

        if (paymentTransactionId != null ? !paymentTransactionId.equals(that.paymentTransactionId) : that.paymentTransactionId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return paymentTransactionId != null ? paymentTransactionId.hashCode() : 0;
    }
}
