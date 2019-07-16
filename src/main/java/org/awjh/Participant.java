package org.awjh;

import org.hyperledger.fabric.contract.annotation.Property;
import org.awjh.ledger_api.State;

public abstract class Participant extends State {
    @Property()
    private String id;

    @Property()
    private String organizationId;

    @Property()
    private String[] roles;

    public Participant(String id, String[] roles, String organizationId, String participantType) {
        super(new String[]{id});

        this.id = id;
        this.roles = roles;
        this.organizationId = organizationId;
    }

    public String getId() {
        return this.id;
    }

    public String getOrganizationId() {
        return this.organizationId;
    }

    public boolean hasRole(String role) {
        for(String loopRole: this.roles){
            if(loopRole.equals(role))
                return true;
        }
        return false;
    }
}
