package org.awjh.ledger_api.lists;

import org.hyperledger.fabric.contract.Context;

import org.awjh.Organization;
import org.awjh.ledger_api.StateList;

public class OrganizationList extends StateList<Organization> {
    public OrganizationList(Context ctx, String listName, Class<? extends Organization>[] classes) {
        super(ctx, listName);

        this.use(classes);
    }
}