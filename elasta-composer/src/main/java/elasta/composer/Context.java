package elasta.composer;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

/**
 * Created by sohan on 5/15/2017.
 */
public interface Context {

    boolean containsKey(String key);

    <T> Optional<T> get(String key);

    Map<String, Object> getMap();

    Context addAll(Map<String, Object> map);
}
