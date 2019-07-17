package org.awjh.ledger_api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ledger.KeyModification;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.json.JSONObject;

public abstract class StateList<T extends State> {
    private String name;
    private Map<String, Class<? extends T>> supportedClasses;
    private Map<String, String[]> collectionsMap;
    private ArrayList<String> collections;
    private Context ctx;

    public StateList(Context ctx, String listName) {
        this.ctx = ctx;
        this.name = listName;
        this.supportedClasses = new HashMap<String, Class<? extends T>>();
        this.collectionsMap = new HashMap<String, String[]>();
        this.collections = new ArrayList<String>();
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

        final String key = this.ctx.getStub().createCompositeKey(this.name, state.getSplitKey()).toString();

        final byte[] worldStateData = state.serialize().getBytes();

        System.out.println("PUT WORLD STATE DATA: " + new String(worldStateData));

        this.ctx.getStub().putState(key, worldStateData);

        for (String collection : this.collectionsMap.get(state.getClass().getName())) {
            final byte[] privateData = state.serialize(collection).getBytes();

            try {
                this.ctx.getStub().putPrivateData(collection, key, privateData);
                System.out.println("PUT PRIVATE DATA: " + collection + new String(privateData));
            } catch (Exception err) {
                System.out.println("COULD NOT PUT IN STORE: " + collection);
                // can't access that store
            }
        }

        System.out.println("MANAGED TO MAKE SOMETHING");
    }

    public T get(String key) throws RuntimeException {
        final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key)).toString();
        final String worldStateData = new String(this.ctx.getStub().getState(ledgerKey));

        System.out.println("WORLD STATE DATA: " + worldStateData);

        if (worldStateData.length() == 0) {
            throw new RuntimeException("Cannot get state. No state exists for key " + key);
        }

        JSONObject worldStateJSON = new JSONObject(worldStateData);
        String stateClass = worldStateJSON.getString("stateClass");

        System.out.println("ABOUT TO START LOOPING THE PRIVATE DATA");

        System.out.println("STATE CLASS => " + stateClass);
        for (Map.Entry<String, String[]> entry: this.collectionsMap.entrySet()) {
            System.out.println("LOOP STATE CLASS => " + entry.getKey());
        }
        if (!this.supportedClasses.containsKey(stateClass)) {
            throw new RuntimeException("Cannot get state for key " + key + ". State class is not in list of supported classes for state list.");
        }

        final Class<? extends T> clazz = this.supportedClasses.get(stateClass);
        System.out.println("Number of collections => " + Integer.toString(this.collectionsMap.size()));

        for (String collection : this.collectionsMap.get(clazz.getName())) {
            try {
                final String privateData = new String(ctx.getStub().getPrivateData(collection, ledgerKey));
                System.out.println("PRIVATE STATE DATA " + collection + ": " + privateData);

                if (privateData.length() > 0) {
                    JSONObject privateJSON = new JSONObject(privateData);

                    for (String jsonKey : JSONObject.getNames(privateJSON)) {
                        worldStateJSON.put(jsonKey, privateJSON.get(jsonKey));
                    }
                }
            } catch (Exception err) {
                // no problem they can't access the data
                System.out.println("PRIVATE DATA STORE => " + collection);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                err.printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.out.println(sStackTrace);
            }
        }

        T returnVal;

        try {
            System.out.println("ATTEMPTING TO DESERIALIZE => " + worldStateJSON.toString());
            returnVal = this.deserialize(worldStateJSON);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string
            System.out.println(sStackTrace);
            throw new RuntimeException("Failed to deserialize" + key + ". " + e.getLocalizedMessage());
        }
        return returnVal;
    }

    @SuppressWarnings("unchecked")
    public HistoricState<T>[] getHistory(String key) {
        // No history for private data
        final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key)).toString();
        final QueryResultsIterator<KeyModification> keyHistory = this.ctx.getStub().getHistoryForKey(ledgerKey);

        ArrayList<HistoricState<T>> hsArrList = new ArrayList<HistoricState<T>>();

        for (KeyModification modification : keyHistory) {
            final String worldStateData = modification.getStringValue();

            JSONObject worldStateJSON = new JSONObject(worldStateData);

            T state;
            try {
                state = this.deserialize(worldStateJSON);
            } catch (RuntimeException e) {
                throw new RuntimeException("Failed to get history for key " + key + ". " + e.getLocalizedMessage());
            }

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

        Map<String, JSONObject> valuesArrMap = new HashMap<String, JSONObject>();

        java.util.function.Consumer<QueryResultsIterator<KeyValue>> iterate = (values) -> {
            for (KeyValue value : values) {
                final String data = value.getStringValue();

                JSONObject json = new JSONObject(data);

                if (valuesArrMap.containsKey(value.getKey())) {
                    final JSONObject existingJSON = valuesArrMap.get(value.getKey());

                    for (String jsonKey : JSONObject.getNames(existingJSON)) {
                        json.put(jsonKey, existingJSON.get(jsonKey));
                    }
                }

                valuesArrMap.put(value.getKey(), json);
            }
        };

        final QueryResultsIterator<KeyValue> worldStateValues = this.ctx.getStub().getQueryResult(query.toString());
        iterate.accept(worldStateValues);

        for (String collection : this.collections) {
            try {
                final QueryResultsIterator<KeyValue> privateValues = this.ctx.getStub().getPrivateDataQueryResult(collection, query.toString());
                iterate.accept(privateValues);
            } catch (Exception err) {
                // can't use that store
            }
        }

        T[] valuesArr = (T[])  new Object[valuesArrMap.size()];

        int counter = 0;
        for (Map.Entry<String, JSONObject> result : valuesArrMap.entrySet()) {
            T state;
            try {
                state = this.deserialize(result.getValue());
            } catch (RuntimeException e) {
                throw new RuntimeException("Failed to run query. " + e.getLocalizedMessage());
            }

            valuesArr[counter] = state;
            counter++;
        }

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

        final byte[] data = state.serialize().getBytes();

        this.ctx.getStub().putState(ledgerKey, data);

        for (String collection : this.collectionsMap.get(state.getClass().getName())) {
            final byte[] privateData = state.serialize(collection).getBytes();

            try {
                this.ctx.getStub().putPrivateData(collection, ledgerKey, privateData);
            } catch (Exception err) {
                // can't access that store
            }
        }
    }

    public void delete(String key) {
        if (this.exists(key)) {
            final T state = this.get(key);

            final String ledgerKey = this.ctx.getStub().createCompositeKey(this.name, State.splitKey(key)).toString();

            this.ctx.getStub().delState(ledgerKey);

            for (String collection : this.collectionsMap.get(state.getClass().getName())) {
                try {
                    this.ctx.getStub().delPrivateData(collection, ledgerKey);
                } catch (Exception err) {
                    // can't access that store
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private String[] getCollections(Class clazz) {
        // don't want to do this everytime. make more efficient
        final ArrayList<String> collections = new ArrayList<String>();

        for (Field field : clazz.getDeclaredFields()) {
            final Private annotation = field.getAnnotation(Private.class);
            if (annotation != null) {
                collections.addAll(Arrays.asList(annotation.collections()));
            }
        }

        return Arrays.stream(collections.toArray(new String[collections.size()])).distinct().toArray(String[]::new);
    }

    protected void use(Class<? extends T> stateClass) {
        this.supportedClasses.put(stateClass.getName(), stateClass);

        String[] collections = this.getCollections(stateClass);
        this.collectionsMap.put(stateClass.getName(), collections);

        for (String collection : collections ) {
            if (!this.collections.contains(collection)) {
                this.collections.add(collection);
            }
        }
    }

    protected void use(Class<? extends T>[] stateClasses) {
        for (Class<? extends T> stateClass : stateClasses) {
            this.use(stateClass);
        }
    }

    @SuppressWarnings("unchecked")
    private T deserialize(JSONObject json) {
        String stateClass = json.getString("stateClass");

        final Class<? extends T> clazz = this.supportedClasses.get(stateClass);
        Method deserialize;
        try {
            deserialize = clazz.getMethod("deserialize", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("State class missing deserialize function");
        }

        T state;
        try {
            System.out.println("Attempting to deserialize => " + json.toString());
            state = (T) deserialize.invoke(null, json.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize. " + e.getMessage());
        }
        return state;
    }
}
