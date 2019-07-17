package org.awjh.ledger_api;

import java.lang.reflect.Field;
import java.util.ArrayList;
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

    private String key;
    private String stateClass;

    public State(String[] keyParts) {
        this.key = State.makeKey(keyParts);
        this.stateClass = this.getClass().getName();
    }

    public String serialize() {
        return this.serialize(null);
    }

    public String serialize(String collection) {
        JSONObject json = new JSONObject();

        Class clazz = this.getClass();
        ArrayList<Field> fields = new ArrayList<Field>();

        do {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        } while ((clazz = clazz.getSuperclass()) != null);

        for (Field field : fields) {
            System.out.println("SERIALIZE FIELD NAME ==> " + field.getName());
            field.setAccessible(true);
            
            if (this.shouldAddToJSON(collection, field)) {
                try {
                    // json.put(field.getName(), field.get(this));
                    Object value = field.get(this);
                    System.out.println("SERIELIZED TYPE => " + value.getClass().getName());
                    if (value instanceof State) {
                        State stateValue = (State) value;
                        json.put(field.getName(), new JSONObject(stateValue.serialize()));
                    } else {
                        json.put(field.getName(), value);
                    }
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

    public String[] getSplitKey() {
        return State.splitKey(this.key);
    }

    private boolean shouldAddToJSON(String collection, Field field) {
        if (collection == null) {
            return field.getAnnotation(Private.class) == null;
        }

        final Private annotation = field.getAnnotation(Private.class);
        return annotation != null && Arrays.asList(annotation.collections()).contains(collection);
    }
}
