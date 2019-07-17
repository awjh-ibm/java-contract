package org.locnet.assets;

import org.awjh.ledger_api.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class ProductDetails extends State {

    public static ProductDetails deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        String productType = jsonObject.getString("productType");
        int quantity = jsonObject.getInt("quantity");
        Double unitPrice = jsonObject.getDouble("unitprice");

        return new ProductDetails(productType, quantity, unitPrice);
    }

    @Property()
    private String productType;

    @Property()
    private int quantity;

    @Property()
    private Double unitPrice;

    public ProductDetails(String productType, int quantity, Double unitPrice) {
        super(new String[]{""});
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

    public Double getUnitPrice() {
        return this.unitPrice;
    }
}
