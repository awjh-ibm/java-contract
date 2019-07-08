package org.locnet.participants;

import org.awjh.Participant;
import org.hyperledger.fabric.contract.annotation.DataType;

@DataType()
public class Task extends Participant {
    public Task(String id, String[] roles, String organizationId) {
        super(id, roles, organizationId, "Task");
    }
}