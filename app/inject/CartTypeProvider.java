package inject;

import com.google.inject.Provider;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.types.Type;
import io.sphere.sdk.types.TypeDraft;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import io.sphere.sdk.types.queries.TypeQuery;

import javax.inject.Inject;

import static io.sphere.sdk.json.SphereJsonUtils.readObjectFromResource;

class CartTypeProvider implements Provider<Type> {

    private final BlockingSphereClient sphereClient;

    @Inject
    public CartTypeProvider(final BlockingSphereClient sphereClient) {
        this.sphereClient = sphereClient;
    }

    @Override
    public Type get() {
        return sphereClient.executeBlocking(TypeQuery.of().withPredicates(type -> type.key().is("cart-frequency-key")))
                .head()
                .orElseGet(this::createProductType);
    }

    private Type createProductType() {
        final TypeDraft typeDraft = readObjectFromResource("data/type-draft.json", TypeDraft.class);
        return sphereClient.executeBlocking(TypeCreateCommand.of(typeDraft));
    }
}
