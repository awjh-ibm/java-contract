package org.awjh.ledger_api;

import org.json.JSONObject;

public abstract class State {
    public static String makeKey(String[] keyParts) {
        return String.join(":", keyParts);
    }

    public static String[] splitKey(String key) {
        return key.split(":");
    }

    private String stateClass;
    private String key;

    public State(String stateClass, String[] keyParts) {
        this.stateClass = stateClass;
        this.key = State.makeKey(keyParts);
    }

    public byte[] serialize() {
        return new JSONObject(this).toString().getBytes();
    }

    public String getKey() {
        return this.key;
    }

    public String getStateClass() {
        return this.stateClass;
    }

    public String[] getSplitKey() {
        return State.splitKey(this.key);
    }
}