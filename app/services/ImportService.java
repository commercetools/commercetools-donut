package services;

import io.sphere.sdk.products.Product;
import play.libs.F;

public interface ImportService {

    F.Promise<Product> exportProductModel();
}
