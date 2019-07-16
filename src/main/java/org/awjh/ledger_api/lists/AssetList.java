package org.awjh.ledger_api.lists;

import org.hyperledger.fabric.contract.Context;

import org.awjh.Asset;
import org.awjh.ledger_api.StateList;

public class AssetList extends StateList<Asset> {
    public AssetList(Context ctx, String listName, Class<Asset>[] classes) {
        super(ctx, listName);

        this.use(classes);
    }
}