package services;

import com.google.inject.Singleton;
import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.PlayJavaSphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.types.*;
import io.sphere.sdk.types.commands.TypeCreateCommand;
import play.libs.F;

import javax.inject.Inject;
import java.util.*;

@Singleton
public class ExporterServiceImpl extends AbstractShopService implements ExporterService {

    @Inject
    public ExporterServiceImpl(final PlayJavaSphereClient playJavaSphereClient) {
        super(playJavaSphereClient);
    }

    @Override
    public F.Promise<Type> createCustomType() {
        final TypeDraft customTypeDraft = frequencyTypeDefinition();
        return playJavaSphereClient().execute(TypeCreateCommand.of(customTypeDraft));
    }

    @Override
    public F.Promise<ProductProjection> createProductModel() {
        throw new UnsupportedOperationException("ExporterService.createProductModel() not implemented yet");
    }

    private static TypeDraft frequencyTypeDefinition() {
        final LocalizedString typeName = LocalizedString.of(Locale.ENGLISH, "custom type for delivery frequency");
        final String key = "cart-frequency-key";
        final String cartResourceTypeId = Cart.resourceTypeId();
        final Set<String> resourceTypeIds = Collections.singleton(cartResourceTypeId);
        final List<FieldDefinition> fieldDefinitions = Arrays.asList(frequencyFieldDefinition());

        return TypeDraftBuilder.of(key, typeName, resourceTypeIds).fieldDefinitions(fieldDefinitions).build();
    }

    private static FieldDefinition frequencyFieldDefinition() {
        final LocalizedString frequencyFieldLabel = LocalizedString.of(Locale.ENGLISH, "selected frequency");
        return FieldDefinition.of(StringType.of(), "frequency", frequencyFieldLabel, false, TextInputHint.SINGLE_LINE);
    }
}
