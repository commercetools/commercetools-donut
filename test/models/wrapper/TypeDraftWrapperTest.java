package models.wrapper;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.TextInputHint;
import io.sphere.sdk.types.FieldDefinition;
import io.sphere.sdk.types.NumberType;
import io.sphere.sdk.types.TypeDraft;
import org.junit.Before;
import org.junit.Test;
import utils.JsonUtils;

import java.util.Locale;

import static org.assertj.core.api.StrictAssertions.assertThat;

public class TypeDraftWrapperTest {

    private static final String TYPE_DRAFT_JSON_RESOURCE = "data/type-draft.json";

    private TypeDraft typeDraft;

    @Before
    public void setUp() {
        final TypeDraftWrapper typeDraftWrapper =
                JsonUtils.readObjectFromResource(TYPE_DRAFT_JSON_RESOURCE, TypeDraftWrapper.class);
        assertThat(typeDraftWrapper).isNotNull();
        typeDraft = typeDraftWrapper.createTypeDraft();
    }

    @Test
    public void testCreateTypeDraft() {
        assertThat(typeDraft).isNotNull();
        assertThat(typeDraft.getName()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "cart-frequency"));
        assertThat(typeDraft.getKey()).isEqualTo("cart-frequency-key");
        assertThat(typeDraft.getDescription()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "cart-frequency description"));
        assertThat(typeDraft.getResourceTypeIds().size()).isEqualTo(1);
        assertThat(typeDraft.getResourceTypeIds().iterator().next()).isEqualTo("order");
        assertThat(typeDraft.getFieldDefinitions().size()).isEqualTo(1);
    }

    @Test
    public void testFieldDefinition() {
        assertThat(typeDraft.getFieldDefinitions().size()).isEqualTo(1);
        final FieldDefinition fieldDefinition = typeDraft.getFieldDefinitions().get(0);
        assertThat(fieldDefinition.getName()).isEqualTo("frequency");
        assertThat(fieldDefinition.getLabel()).isEqualTo(LocalizedString.of(Locale.ENGLISH, "selected frequency"));
        assertThat(fieldDefinition.getType()).isEqualTo(NumberType.of());
        assertThat(fieldDefinition.getInputHint()).isEqualTo(TextInputHint.SINGLE_LINE);
        assertThat(fieldDefinition.isRequired()).isEqualTo(true);
    }
}
