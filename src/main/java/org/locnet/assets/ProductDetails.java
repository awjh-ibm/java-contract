package org.locnet.assets;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class ProductDetails {

    public static ProductDetails deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        String productType = jsonObject.getString("productType");
        int quantity = jsonObject.getInt("quantity");
        int unitPrice = jsonObject.getInt("unitprice");

        return new ProductDetails(productType, quantity, unitPrice);
    }

    @Property()
    private String productType;

    @Property()
    private int quantity;

    @Property()
    private int unitPrice;

    public ProductDetails(String productType, int quantity, int unitPrice) {
        this.productType = productType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductType() {
        return this.productType;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public int getUnitPrice() {
        return this.unitPrice;
    }
}