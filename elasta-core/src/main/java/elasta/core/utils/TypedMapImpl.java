package elasta.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Jango on 11/5/2016.
 */
public class TypedMapImpl implements TypedMap {

    private final Map<String, Object> map;

    /**
     * Create a new, empty instance
     */
    public TypedMapImpl() {
        map = new LinkedHashMap<>();
    }

    /**
     * Create an instance from a Map. The Map is not copied.
     *
     * @param map the map to create the instance from.
     */
    public TypedMapImpl(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * Get the string value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a String
     */
    public String getString(String key) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) map.get(key);
        return cs == null ? null : cs.toString();
    }

    /**
     * Get the Integer value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not an Integer
     */
    public Integer getInteger(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Integer) {
            return (Integer) number;  // Avoids unnecessary unbox/box
        } else {
            return number.intValue();
        }
    }

    /**
     * Get the Long value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Long
     */
    public Long getLong(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Long) {
            return (Long) number;  // Avoids unnecessary unbox/box
        } else {
            return number.longValue();
        }
    }

    /**
     * Get the Double value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Double
     */
    public Double getDouble(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Double) {
            return (Double) number;  // Avoids unnecessary unbox/box
        } else {
            return number.doubleValue();
        }
    }

    /**
     * Get the Float value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Float
     */
    public Float getFloat(String key) {
        Objects.requireNonNull(key);
        Number number = (Number) map.get(key);
        if (number == null) {
            return null;
        } else if (number instanceof Float) {
            return (Float) number;  // Avoids unnecessary unbox/box
        } else {
            return number.floatValue();
        }
    }

    /**
     * Get the Boolean value with the specified key
     *
     * @param key the key to return the value for
     * @return the value or null if no value for that key
     * @throws java.lang.ClassCastException if the value is not a Boolean
     */
    public Boolean getBoolean(String key) {
        Objects.requireNonNull(key);
        return (Boolean) map.get(key);
    }

    /**
     * Get the value with the specified key, as an Object
     *
     * @param key the key to lookup
     * @return the value
     */
    public <T> T getValue(String key) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return (T) val;
    }

    /**
     * Like {@link #getString(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public String getString(String key, String def) {
        Objects.requireNonNull(key);
        CharSequence cs = (CharSequence) map.get(key);
        return cs != null || map.containsKey(key) ? cs == null ? null : cs.toString() : def;
    }

    /**
     * Like {@link #getInteger(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Integer getInteger(String key, Integer def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Integer) {
            return (Integer) val;  // Avoids unnecessary unbox/box
        } else {
            return val.intValue();
        }
    }

    /**
     * Like {@link #getLong(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Long getLong(String key, Long def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Long) {
            return (Long) val;  // Avoids unnecessary unbox/box
        } else {
            return val.longValue();
        }
    }

    /**
     * Like {@link #getDouble(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Double getDouble(String key, Double def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Double) {
            return (Double) val;  // Avoids unnecessary unbox/box
        } else {
            return val.doubleValue();
        }
    }

    /**
     * Like {@link #getFloat(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Float getFloat(String key, Float def) {
        Objects.requireNonNull(key);
        Number val = (Number) map.get(key);
        if (val == null) {
            if (map.containsKey(key)) {
                return null;
            } else {
                return def;
            }
        } else if (val instanceof Float) {
            return (Float) val;  // Avoids unnecessary unbox/box
        } else {
            return val.floatValue();
        }
    }

    /**
     * Like {@link #getBoolean(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public Boolean getBoolean(String key, Boolean def) {
        Objects.requireNonNull(key);
        Object val = map.get(key);
        return val != null || map.containsKey(key) ? (Boolean) val : def;
    }

    /**
     * Like {@link #getValue(String)} but specifying a default value to return if there is no entry.
     *
     * @param key the key to lookup
     * @param def the default value to use if the entry is not present
     * @return the value or {@code def} if no entry present
     */
    public <T> T getValue(String key, T def) {
        Objects.requireNonNull(key);
        Object val = getValue(key);
        return (T) (val != null || map.containsKey(key) ? val : def);
    }

    /**
     * Does the JSON object contain the specified key?
     *
     * @param key the key
     * @return true if it contains the key, false if not.
     */
    public boolean containsKey(String key) {
        Objects.requireNonNull(key);
        return map.containsKey(key);
    }

    /**
     * Return the set of field names in the JSON objects
     *
     * @return the set of field names
     */
    public Set<String> fieldNames() {
        return map.keySet();
    }

    /**
     * Put an Enum into the JSON object with the specified key.
     * <p>
     * JSON has no concept of encoding Enums, so the Enum will be converted to a String using the {@link java.lang.Enum#name}
     * method and the value put as a String.
     *
     * @param key   the key
     * @param value the value
     * @return a reference to this, so the API can be used fluently
     */
    public <T> TypedMap put(String key, T value) {
        Objects.requireNonNull(key);
        map.put(key, value == null ? null : value);
        return this;
    }

    /**
     * Put a null value into the JSON object with the specified key.
     *
     * @param key the key
     * @return a reference to this, so the API can be used fluently
     */
    public TypedMap putNull(String key) {
        Objects.requireNonNull(key);
        map.put(key, null);
        return this;
    }

    /**
     * Remove an entry from this object.
     *
     * @param key the key
     * @return the value that was removed, or null if none
     */
    public Object remove(String key) {
        return map.remove(key);
    }

    /**
     * Get the number of entries in the JSON object
     *
     * @return the number of entries
     */
    public int size() {
        return map.size();
    }

    public TypedMap clear() {
        map.clear();
        return this;
    }

    /**
     * Is this object entry?
     *
     * @return true if it has zero entries, false if not.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedMapImpl typedMap = (TypedMapImpl) o;

        return map != null ? map.equals(typedMap.map) : typedMap.map == null;

    }

    @Override
    public int hashCode() {
        return map != null ? map.hashCode() : 0;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
