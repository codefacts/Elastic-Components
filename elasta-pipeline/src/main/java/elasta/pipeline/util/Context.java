package elasta.pipeline.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by shahadat on 4/27/16.
 */
final public class Context {
    protected final Map<String, Object> map;

    public Context() {
        map = Collections.EMPTY_MAP;
    }

    public Context(Map<String, Object> map) {
        this.map = map == null ? Collections.EMPTY_MAP : map;
    }

    //Methods
    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Collection<Object> values() {
        return map.values();
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    //MyMethods
    public <T> T as(String key) {
        Objects.requireNonNull(key);
        return (T) map.get(key);
    }

    public <T> T as(String key, T defaultValue) {
        Objects.requireNonNull(key);
        final Object o = map.get(key);
        return o == null ? defaultValue : (T) o;
    }

    public JsonObject getJsonObject(String key) {
        Objects.requireNonNull(key);
        return (JsonObject) map.get(key);
    }

    public JsonObject getJsonObject(String key, JsonObject dv) {
        Objects.requireNonNull(key);
        final Object val = map.get(key);
        return val == null ? dv : (JsonObject) val;
    }

    public JsonArray getJsonArray(String key) {
        Objects.requireNonNull(key);
        return (JsonArray) map.get(key);
    }

    public JsonArray getJsonArray(String key, JsonArray dv) {
        Objects.requireNonNull(key);
        final Object o = map.get(key);
        return o == null ? dv : (JsonArray) o;
    }

    public String getString(String key) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) this.map.get(key);
        return cs == null ? null : cs.toString();
    }

    public Integer getInteger(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) this.map.get(key);
        return number == null ? null : (number instanceof Integer ? (Integer) number : Integer.valueOf(number.intValue()));
    }

    public Long getLong(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) this.map.get(key);
        return number == null ? null : (number instanceof Long ? (Long) number : Long.valueOf(number.longValue()));
    }

    public Double getDouble(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) this.map.get(key);
        return number == null ? null : (number instanceof Double ? (Double) number : Double.valueOf(number.doubleValue()));
    }

    public Float getFloat(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) this.map.get(key);
        return number == null ? null : (number instanceof Float ? (Float) number : Float.valueOf(number.floatValue()));
    }

    public Boolean getBoolean(String key) {
        Objects.requireNonNull(key);
        return (Boolean) this.map.get(key);
    }

    public String getString(String key, String def) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) this.map.get(key);
        return cs == null && !this.map.containsKey(key) ? def : (cs == null ? null : cs.toString());
    }

    public Integer getInteger(String key, Integer def) {
        Objects.requireNonNull(key);
        Number val = (Number) this.map.get(key);
        return val == null ? (this.map.containsKey(key) ? null : def) : (val instanceof Integer ? (Integer) val : Integer.valueOf(val.intValue()));
    }

    public Long getLong(String key, Long def) {
        Objects.requireNonNull(key);
        Number val = (Number) this.map.get(key);
        return val == null ? (this.map.containsKey(key) ? null : def) : (val instanceof Long ? (Long) val : Long.valueOf(val.longValue()));
    }

    public Double getDouble(String key, Double def) {
        Objects.requireNonNull(key);
        Number val = (Number) this.map.get(key);
        return val == null ? (this.map.containsKey(key) ? null : def) : (val instanceof Double ? (Double) val : Double.valueOf(val.doubleValue()));
    }

    public Float getFloat(String key, Float def) {
        Objects.requireNonNull(key);
        Number val = (Number) this.map.get(key);
        return val == null ? (this.map.containsKey(key) ? null : def) : (val instanceof Float ? (Float) val : Float.valueOf(val.floatValue()));
    }

    public Boolean getBoolean(String key, Boolean def) {
        Objects.requireNonNull(key);
        Object val = this.map.get(key);
        return val == null && !this.map.containsKey(key) ? def : (Boolean) val;
    }

    public static void main(String[] args) {

    }
}
