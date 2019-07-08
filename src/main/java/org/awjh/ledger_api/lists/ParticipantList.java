package org.awjh.ledger_api.lists;

import java.util.function.Function;
import java.util.Map;

import org.hyperledger.fabric.contract.Context;

import org.awjh.Participant;
import org.awjh.ledger_api.StateList;

public class ParticipantList extends StateList<Participant> {
    public ParticipantList(Context ctx, String listName, Map<String, Function<String, Participant>> deserializers) {
        super(ctx, listName);

        this.use(deserializers);
    }
}