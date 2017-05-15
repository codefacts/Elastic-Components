package elasta.composer;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

/**
 * Created by sohan on 5/12/2017.
 */
public interface Headers {

    boolean containsKey(String key);

    Set<String> keySet();

    Optional<Integer> getInt(String key);

    Optional<Long> getLong(String key);

    Optional<Float> getFloat(String key);

    Optional<Double> getDouble(String key);

    Optional<String> getString(String key);

    Optional<Date> getDate(String key);

    List<String> getAll(String key);

    ListMultimap<String, String> getMultimap();

    Headers addAll(ListMultimap<String, String> multimap);

    Headers addAll(Map<String, String> multimap);
}
