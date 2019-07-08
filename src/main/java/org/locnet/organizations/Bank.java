package org.locnet.organizations;

import org.awjh.Organization;
import org.json.JSONObject;

public class Bank extends Organization {
    public static Bank deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        String id = jsonObject.getString("id");
        String name = jsonObject.getString("name");

        return new Bank(id, name);
    }

    public Bank(String id, String name) {
        super(id, name, "Bank");
    }
}