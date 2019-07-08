package org.awjh;

import org.hyperledger.fabric.contract.annotation.Property;
import org.locnet.Constants;
import org.awjh.ledger_api.State;

public abstract class Asset extends State {
    public static String generateClass(String assetType) {
        return Constants.NETWORK_NAME + ".assets."  + assetType;
    }

    public static Asset deserialize(String json) {
        throw new RuntimeException("Not yet implemented");
    };

    @Property()
    private String id;

    public Asset(String id, String assetType) {
        super(Asset.generateClass(assetType), new String[]{id});
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}