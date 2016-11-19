package elasta.commons;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 2016-11-19.
 */
public class NestedMapBuilderImpl<T> implements NestedMapBuilder<T> {
    private final Map<String, Object> map = createMap();

    @Override
    public NestedMapBuilder<T> put(List<String> path, T value) {

        Map<String, Object> mm = map;
        for (int i = 0, pathSize_1 = path.size() - 1; i < pathSize_1; i++) {
            String part = path.get(i);
            Map<String, Object> pp = (Map<String, Object>) mm.get(part);
            if (pp == null) {
                pp = createMap();
                mm.put(part, pp);
            }
            mm = pp;
        }

        mm.put(path.get(path.size() - 1), value);

        return this;
    }

    @Override
    public Map<String, Object> createMap() {
        return new LinkedHashMap<>();
    }

    @Override
    public Map<String, Object> build() {
        return map;
    }
}
