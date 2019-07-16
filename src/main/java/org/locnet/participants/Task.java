package org.locnet.participants;

import org.awjh.Participant;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.json.JSONArray;
import org.json.JSONObject;

@DataType()
public class Task extends Participant {
    public static Task deserialize(String json) {
        JSONObject jsonObject = new JSONObject(json);

        String id = jsonObject.getString("id");
        String organizationId = jsonObject.getString("organizationId");
        JSONArray rolesArr = jsonObject.getJSONArray("roles");
        String[] roles = new String[rolesArr.length()];
        for (int i = 0; i < rolesArr.length(); i++) {
            roles[i] = rolesArr.getString(i);
        }

        return new Task(id, roles, organizationId);
    }

    public Task(String id, String[] roles, String organizationId) {
        super(id, roles, organizationId, "Task");
    }
}