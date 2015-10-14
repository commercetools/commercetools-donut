package services;

import io.sphere.sdk.products.Product;
import io.sphere.sdk.types.Type;
import play.libs.F;

public interface ImportService {

    F.Promise<Product> exportProductModel();

    F.Promise<Type> exportCustomType();
}
