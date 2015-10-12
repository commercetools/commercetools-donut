package services;

import io.sphere.sdk.products.Product;
import io.sphere.sdk.types.Type;
import play.libs.F;

public interface ExportService {

    F.Promise<Product> exportProductModel();

    F.Promise<Type> exportCustomType();
}
