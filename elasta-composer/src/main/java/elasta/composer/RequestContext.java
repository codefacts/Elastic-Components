package elasta.composer;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sohan on 5/12/2017.
 */
public interface RequestContext {

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
}
