package org.awjh.ledger_api.lists;

import java.util.function.Function;
import java.util.Map;

import org.hyperledger.fabric.contract.Context;

import org.awjh.Asset;
import org.awjh.ledger_api.StateList;

public class AssetList extends StateList<Asset> {
    public AssetList(Context ctx, String listName, Map<String, Function<String, Asset>> deserializers) {
        super(ctx, listName);

        this.use(deserializers);
    }
}