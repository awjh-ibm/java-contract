package org.awjh.ledger_api;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class State {

    public static String makeKey(String[] keyParts) {
        return String.join(":", keyParts);
    }

    public static String[] splitKey(String key) {
        return key.split(":");
    }

    public static State deserialize(String json) {
        throw new RuntimeException("Not yet implemented");
    };

    private String stateClass;
    private String key;

    public State(String stateClass, String[] keyParts) {
        this.stateClass = stateClass;
        this.key = State.makeKey(keyParts);
    }

    public String serialize() {
        JSONObject json = new JSONObject();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Private.class) == null) {
                try {
                    json.put(field.getName(), field.get(this));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return json.toString();
    }

    public String serialize(String collection) {
        JSONObject json = new JSONObject();

        for (Field field : this.getClass().getDeclaredFields()) {
            final Private annotation = field.getAnnotation(Private.class);
            if (annotation != null && Arrays.asList(annotation.collections()).contains(collection)) {
                try {
                    json.put(field.getName(), field.get(this));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return json.toString();
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