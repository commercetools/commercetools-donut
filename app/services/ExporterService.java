package services;

import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.types.Type;
import play.libs.F;

public interface ExporterService {

    F.Promise<Type> createCustomType();

    F.Promise<ProductProjection> createProductModel();
}
