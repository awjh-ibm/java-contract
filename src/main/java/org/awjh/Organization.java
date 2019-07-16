package org.awjh;

import org.hyperledger.fabric.contract.annotation.Property;
import org.awjh.ledger_api.State;

public abstract class Organization extends State {
    public static Organization deserialize(String json) {
        throw new RuntimeException("Not yet implemented");
    };

    @Property()
    private String id;

    @Property()
    private String name;

    public Organization(String id, String name, String organizationType) {
        super(new String[]{id});

        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
