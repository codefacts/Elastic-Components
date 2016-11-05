package elasta.core;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Jango on 11/5/2016.
 */
public interface TypedMap {
    public String getString(String key);

    public Integer getInteger(String key);

    public Long getLong(String key);

    public Double getDouble(String key);

    public Float getFloat(String key);

    public Boolean getBoolean(String key);

    public <T> T getValue(String key);

    public String getString(String key, String def);

    public Integer getInteger(String key, Integer def);

    public Long getLong(String key, Long def);

    public Double getDouble(String key, Double def);

    public Float getFloat(String key, Float def);

    public Boolean getBoolean(String key, Boolean def);

    public <T> T getValue(String key, T def);

    public boolean containsKey(String key);

    public Set<String> fieldNames();

    public <T> TypedMap put(String key, T value);

    public TypedMap putNull(String key);

    public Object remove(String key);

    public int size();

    public TypedMap clear();

    public boolean isEmpty();
}
