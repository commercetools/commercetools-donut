package pactas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PactasInvoice {
    private String recipientName;
    private PostalAddress recipientAddress;
    private List<InvoiceItemList> itemList;
    private String currency;
    private String totalNet;
    private String totalVat;
    private String totalGross;

    private PactasInvoice(@JsonProperty("RecipientName") String recipientName,
                          @JsonProperty("RecipientAddress") PostalAddress recipientAddress,
                          @JsonProperty("ItemList") List<InvoiceItemList> itemList,
                          @JsonProperty("Currency") String currency,
                          @JsonProperty("TotalNet") String totalNet,
                          @JsonProperty("TotalVat") String totalVat,
                          @JsonProperty("TotalGross") String totalGross) {
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.itemList = itemList;
        this.currency = currency;
        this.totalNet = totalNet;
        this.totalVat = totalVat;
        this.totalGross = totalGross;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public PostalAddress getRecipientAddress() {
        return recipientAddress;
    }

    public List<InvoiceItemList> getItemList() {
        return itemList;
    }

    public Currency getCurrency() {
        return Currency.getInstance(currency);
    }

    public BigDecimal getTotalNet() {
        return new BigDecimal(totalNet);
    }

    public BigDecimal getTotalVat() {
        return new BigDecimal(totalVat);
    }

    public BigDecimal getTotalGross() {
        return new BigDecimal(totalGross);
    }

    @Override
    public String toString() {
        return "PactasInvoice{" +
                "recipientName='" + recipientName + '\'' +
                ", recipientAddress=" + recipientAddress +
                ", itemList=" + itemList +
                ", currency='" + currency + '\'' +
                ", totalNet='" + totalNet + '\'' +
                ", totalVat='" + totalVat + '\'' +
                ", totalGross='" + totalGross + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PactasInvoice that = (PactasInvoice) o;

        if (currency != null ? !currency.equals(that.currency) : that.currency != null) return false;
        if (itemList != null ? !itemList.equals(that.itemList) : that.itemList != null) return false;
        if (recipientAddress != null ? !recipientAddress.equals(that.recipientAddress) : that.recipientAddress != null)
            return false;
        if (recipientName != null ? !recipientName.equals(that.recipientName) : that.recipientName != null)
            return false;
        if (totalGross != null ? !totalGross.equals(that.totalGross) : that.totalGross != null) return false;
        if (totalNet != null ? !totalNet.equals(that.totalNet) : that.totalNet != null) return false;
        if (totalVat != null ? !totalVat.equals(that.totalVat) : that.totalVat != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = recipientName != null ? recipientName.hashCode() : 0;
        result = 31 * result + (recipientAddress != null ? recipientAddress.hashCode() : 0);
        result = 31 * result + (itemList != null ? itemList.hashCode() : 0);
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        result = 31 * result + (totalNet != null ? totalNet.hashCode() : 0);
        result = 31 * result + (totalVat != null ? totalVat.hashCode() : 0);
        result = 31 * result + (totalGross != null ? totalGross.hashCode() : 0);
        return result;
    }
}
