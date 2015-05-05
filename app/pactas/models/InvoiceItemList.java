package pactas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceItemList {
    private String productId;
    private String description;
    private Integer quantity;

    private InvoiceItemList(@JsonProperty("ProductId") String productId,
                            @JsonProperty("Description") String description,
                            @JsonProperty("Quantity") Integer quantity) {
        this.productId = productId;
        this.description = description;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "PactasItemList{" +
                "productId='" + productId + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceItemList that = (InvoiceItemList) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        return result;
    }
}
