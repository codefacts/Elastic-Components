package elasta.commons;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 2016-11-19.
 */
public interface NestedMapBuilder<T> {

    NestedMapBuilder<T> put(String path, T value);

    Map<String, Object> createMap();

    List<Object> createList();

    Map<String, Object> build();
}
