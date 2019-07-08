package org.locnet.assets;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class Evidence {
    public static Evidence deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        String name = jsonObject.getString("name");
        String hash = jsonObject.getString("hash");

        return new Evidence(name, hash);
    }

    @Property()
    private String name;

    @Property()
    private String hash;

    public Evidence(String name, String hash) {
        this.name = name;
        this.hash = hash;
    }

    public String getName() {
        return this.name;
    }

    public String getHash() {
        return this.hash;
    }
}