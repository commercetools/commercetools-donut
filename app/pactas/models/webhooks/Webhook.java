package pactas.models.webhooks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "Event")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WebhookAccountCreated.class, name = "AccountCreated")})

public interface Webhook {
}
