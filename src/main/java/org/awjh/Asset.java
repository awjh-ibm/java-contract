package org.awjh;

import org.hyperledger.fabric.contract.annotation.Property;
import org.awjh.ledger_api.State;

public abstract class Asset extends State {
    @Property()
    private String id;

    public Asset(String id) {
        super(new String[]{id});
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
