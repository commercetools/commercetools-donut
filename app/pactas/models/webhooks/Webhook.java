package pactas.models.webhooks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "Event")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WebhookAccountCreated.class, name = "AccountCreated"),
        @JsonSubTypes.Type(value = WebhookCustomerChanged.class, name = "CustomerChanged"),
        @JsonSubTypes.Type(value = WebhookPaymentSucceeded.class, name = "PaymentSucceeded") })

public interface Webhook {
}
