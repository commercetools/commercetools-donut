package models.export;

import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.models.Reference;
import io.sphere.sdk.products.*;
import io.sphere.sdk.products.attributes.*;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeDraft;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

public final class ShopData {

    public static ProductTypeDraft productTypeDraft() {
        return ProductTypeDraft.of("Donuts box", "A box with delicious donuts",
                Arrays.asList(quantityAttributeDefinition(), boxAttributeDefinition(), pactasMonthlyAttributeDefinition(),
                        pactasTwoWeeklyAttributeDefinition(), pactasWeeklyAttributeDefinition()));
    }

    public static ProductDraft productDraft(final ProductType productType) {
        final String name = "Classic box";
        final LocalizedString slug = LocalizedString.of(Locale.ENGLISH, name).slugifiedUnique();
        final Reference<ProductType> productTypeReference = Reference.of("product-type", productType.getId(),
                productType);
        return ProductDraftBuilder.of(productTypeReference, LocalizedString.of(Locale.ENGLISH, name), slug, smallBox())
                .description(LocalizedString.of(Locale.ENGLISH, "Box with classic flavours."))
                .variants(Arrays.asList(mediumBox(), largeBox(), hugeBox()))
                .build();
    }

    private static ProductVariantDraft smallBox() {
        final Price price = PriceBuilder.of(Money.of(BigDecimal.valueOf(6.99), "EUR")).build();
        final Image image1 = Image.ofWidthAndHeight("donuts.png", 264, 190, "donuts image label");
        final Image image2 = Image.ofWidthAndHeight("stamp-green.png", 404, 800, "stamp-green image label");
        final Image image3 = Image.ofWidthAndHeight("save-green.png", 213, 100, "save-green image label");

        return ProductVariantDraftBuilder.of().price(price).images(Arrays.asList(image1, image2, image3)).sku("box6")
                .plusAttribute(quantityAttributeDefinition().getName(), 6)
                .plusAttribute(boxAttributeDefinition().getName(), "Small box")
                .plusAttribute(pactasMonthlyAttributeDefinition().getName(), "53284db351f459b0d07df62c")
                .plusAttribute(pactasTwoWeeklyAttributeDefinition().getName(), "53284d0c51f459b0d07df624")
                .plusAttribute(pactasWeeklyAttributeDefinition().getName(), "53284c7751f459b0d07df61a").build();
    }

    private static ProductVariantDraft mediumBox() {
        final Price price = PriceBuilder.of(Money.of(BigDecimal.valueOf(12.99), "EUR")).build();
        final Image image1 = Image.ofWidthAndHeight("donuts.png", 264, 190, "donuts image label");
        final Image image2 = Image.ofWidthAndHeight("stamp-brown.png", 404, 800, "stamp-brown image label");
        final Image image3 = Image.ofWidthAndHeight("save-brown.png", 213, 100, "save-brown image label");

        return ProductVariantDraftBuilder.of().price(price).images(Arrays.asList(image1, image2, image3)).sku("box12")
                .plusAttribute(quantityAttributeDefinition().getName(), 12)
                .plusAttribute(boxAttributeDefinition().getName(), "Medium box")
                .plusAttribute(pactasMonthlyAttributeDefinition().getName(), "53284f5d51f459b0d07df649")
                .plusAttribute(pactasTwoWeeklyAttributeDefinition().getName(), "53284ea051f459b0d07df645")
                .plusAttribute(pactasWeeklyAttributeDefinition().getName(), "53284e7651f459b0d07df643").build();
    }

    private static ProductVariantDraft largeBox() {
        final Price price = PriceBuilder.of(Money.of(BigDecimal.valueOf(23.99), "EUR")).build();
        final Image image1 = Image.ofWidthAndHeight("donuts.png", 264, 190, "donuts image label");
        final Image image2 = Image.ofWidthAndHeight("stamp-pink.png", 404, 800, "stamp-pink image label");
        final Image image3 = Image.ofWidthAndHeight("save-pink.png", 213, 100, "save-pink image label");

        return ProductVariantDraftBuilder.of().price(price).images(Arrays.asList(image1, image2, image3)).sku("box24")
                .plusAttribute(quantityAttributeDefinition().getName(), 24)
                .plusAttribute(boxAttributeDefinition().getName(), "Large box")
                .plusAttribute(pactasMonthlyAttributeDefinition().getName(), "5328507751f459b0d07df65a")
                .plusAttribute(pactasTwoWeeklyAttributeDefinition().getName(), "5328505c51f459b0d07df658")
                .plusAttribute(pactasWeeklyAttributeDefinition().getName(), "53284fd451f459b0d07df654").build();
    }

    private static ProductVariantDraft hugeBox() {
        final Price price = PriceBuilder.of(Money.of(BigDecimal.valueOf(34.99), "EUR")).build();
        final Image image1 = Image.ofWidthAndHeight("donuts.png", 264, 190, "donuts image label");
        final Image image2 = Image.ofWidthAndHeight("stamp-gold.png", 404, 800, "stamp-gold image label");
        final Image image3 = Image.ofWidthAndHeight("save-gold.png", 213, 100, "save-gold image label");

        return ProductVariantDraftBuilder.of().price(price).images(Arrays.asList(image1, image2, image3)).sku("box24")
                .plusAttribute(quantityAttributeDefinition().getName(), 36)
                .plusAttribute(boxAttributeDefinition().getName(), "Huge box")
                .plusAttribute(pactasMonthlyAttributeDefinition().getName(), "5328510651f459b0d07df669")
                .plusAttribute(pactasTwoWeeklyAttributeDefinition().getName(), "532850d351f459b0d07df667")
                .plusAttribute(pactasWeeklyAttributeDefinition().getName(), "532850ac51f459b0d07df661").build();
    }

    private static AttributeDefinition quantityAttributeDefinition() {
        return AttributeDefinitionBuilder.of("quantity", LocalizedString.of(Locale.ENGLISH, "quantity label"),
                NumberType.of()).isRequired(true).attributeConstraint(AttributeConstraint.COMBINATION_UNIQUE).build();
    }

    private static AttributeDefinition boxAttributeDefinition() {
        return AttributeDefinitionBuilder.of("box", LocalizedString.of(Locale.ENGLISH, "box label"),
                StringType.of()).isRequired(true).
                attributeConstraint(AttributeConstraint.COMBINATION_UNIQUE).build();
    }

    private static AttributeDefinition pactasMonthlyAttributeDefinition() {
        return AttributeDefinitionBuilder.of("pactas1", LocalizedString.of(Locale.ENGLISH, "pactas1 monthly"),
                StringType.of()).isRequired(true).attributeConstraint(AttributeConstraint.UNIQUE).build();
    }

    private static AttributeDefinition pactasTwoWeeklyAttributeDefinition() {
        return AttributeDefinitionBuilder.of("pactas2", LocalizedString.of(Locale.ENGLISH, "pactas2 two weekly"),
                StringType.of()).isRequired(true).attributeConstraint(AttributeConstraint.UNIQUE).build();
    }

    private static AttributeDefinition pactasWeeklyAttributeDefinition() {
        return AttributeDefinitionBuilder.of("pactas4", LocalizedString.of(Locale.ENGLISH, "pactas4 weekly"),
                StringType.of()).isRequired(true).attributeConstraint(AttributeConstraint.UNIQUE).build();
    }

    private ShopData() {
    }
}
