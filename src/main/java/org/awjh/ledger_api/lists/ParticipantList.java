package org.awjh.ledger_api.lists;

import org.hyperledger.fabric.contract.Context;

import org.awjh.Participant;
import org.awjh.ledger_api.StateList;

public class ParticipantList extends StateList<Participant> {
    public ParticipantList(Context ctx, String listName, Class<Participant>[] classes) {
        super(ctx, listName);

        this.use(classes);
    }
}