package org.awjh.ledger_api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.json.JSONObject;

public abstract class StateList<T extends State> {
    private String name;
    private Map<String, Function<String, T>> supportedClasses;
    private Context ctx;

    public StateList(Context ctx, String listName) {
        this.ctx = ctx;
        this.name = listName;
        this.supportedClasses = new HashMap<String, Function<String, T>>();
    }

    public boolean exists(String key) {
        try {
            this.get(key);
            return true;
        } catch (Exception err) {
            return false;
        }
    }

    public void add(T state) throws RuntimeException {
        final String stateKey = state.getKey();

        if (this.exists(stateKey)) {
            throw new RuntimeException("Cannot add state. State already exists for key " + stateKey);
        }

        final byte[] data = state.serialize();

        final String key = this.ctx.getStub().createCompositeKey(this.name, state.getSplitKey()).toString();

        this.ctx.getStub().putState(key, data);
    }

    public T get(String key) throws RuntimeException {
        final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key)).toString();
        final String data = this.ctx.getStub().getState(ledgerKey).toString();

        if (data.length() == 0) {
            throw new RuntimeException("Cannot get state. No state exists for key " + key);
        }

        JSONObject json = new JSONObject(data);
        Function<String, T> deserializer = this.supportedClasses.get(json.getString("stateClass"));

        return deserializer.apply(data);
    }

    @SuppressWarnings("unchecked")
    public HistoricState<T>[] getHistory(String key) {
        final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key)).toString();
        final QueryResultsIterator<KeyModification> keyHistory = this.ctx.getStub().getHistoryForKey(ledgerKey);

        ArrayList<HistoricState<T>> hsArrList = new ArrayList<HistoricState<T>>();

        for (KeyModification modification : keyHistory) {
            final String data = modification.getStringValue();

            JSONObject json = new JSONObject(data);
            Function<String, T> deserializer = this.supportedClasses.get(json.getString("stateClass"));

            final T state = deserializer.apply(data);
            final Long ts = modification.getTimestamp().toEpochMilli();
            final String txId = modification.getTxId();

            final HistoricState<T> hs = new HistoricState<T>(ts, txId, state);

            hsArrList.add(hs);
        }

        HistoricState<T>[] hsArr = hsArrList.toArray(new HistoricState[hsArrList.size()]);

        return hsArr;
    }

    @SuppressWarnings("unchecked")
    public T[] query(JSONObject query) {
        if (!query.has("selector")) {
            query.put("selector", new JSONObject());
        }

        query.getJSONObject("selector").put("_id", new JSONObject());
        query.getJSONObject("selector").getJSONObject("_id").put("$regex", ".*" +  this.name + ".*");

        final QueryResultsIterator<KeyValue> values = this.ctx.getStub().getQueryResult(query.toString());

        ArrayList<T> valuesArrList = new ArrayList<T>();

        for (KeyValue value : values) {
            final String data = value.getStringValue();

            JSONObject json = new JSONObject(data);
            Function<String, T> deserializer = this.supportedClasses.get(json.getString("stateClass"));

            final T state = deserializer.apply(data);

            valuesArrList.add(state);
        }

        T[] valuesArr = valuesArrList.toArray((T[]) new Object[valuesArrList.size()]);

        return valuesArr;
    }

    public T[] getAll() {
        return this.query(new JSONObject());
    }

    @SuppressWarnings("unused")
    public int count() {
        final QueryResultsIterator<KeyValue> values = this.ctx.getStub().getStateByPartialCompositeKey(this.name);

        int counter = 0;
        for (KeyValue ignore : values) {
            counter++;
        }

        return counter;
    }

    public void update(T state) {
        this.update(state, false);
    }

    public void update(T state, boolean force) throws RuntimeException {
        final String stateKey = state.getKey();

        if (this.exists(stateKey) && !force) {
            throw new RuntimeException("Cannot update state. No state exists for key " + stateKey);
        }

        final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, state.getSplitKey()).toString();

        final byte[] data = state.serialize();

        this.ctx.getStub().putState(ledgerKey, data);
    }

    public void delete(String key) {
        final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key)).toString();

        this.ctx.getStub().delState(ledgerKey);
    }

    protected void use(String stateClass, Function<String, T> deserializer) {
        this.supportedClasses.put(stateClass, deserializer);
    }

    protected void use(Map<String, Function<String, T>> deserializers) {
        this.supportedClasses.putAll(deserializers);
    }
}
