package services;

import io.sphere.sdk.products.Product;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.types.Type;
import play.libs.F;

public interface ExportService {

    F.Promise<Product> createProductModel();

    F.Promise<ProductType> createProductTypeModel();

    F.Promise<Type> createCustomType();
}
