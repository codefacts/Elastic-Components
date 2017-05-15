package elasta.composer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import io.vertx.core.MultiMap;

import java.util.*;

/**
 * Created by sohan on 5/15/2017.
 */
final public class VertxMultiMap implements MultiMap {
    final ListMultimap<String, String> multimap;

    public VertxMultiMap(ListMultimap<String, String> multimap) {
        Objects.requireNonNull(multimap);
        this.multimap = multimap;
    }

    @Override
    public String get(CharSequence name) {
        return getOne(String.valueOf(name));
    }

    private String getOne(String key) {
        List<String> list = multimap.get(String.valueOf(key));
        if (list.size() <= 0) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public String get(String name) {
        return getOne(name);
    }

    @Override
    public List<String> getAll(String name) {
        return multimap.get(name);
    }

    @Override
    public List<String> getAll(CharSequence name) {
        return multimap.get(String.valueOf(name));
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        return ImmutableList.copyOf(multimap.entries());
    }

    @Override
    public boolean contains(String name) {
        return multimap.containsKey(name);
    }

    @Override
    public boolean contains(CharSequence name) {
        return multimap.containsKey(String.valueOf(name));
    }

    @Override
    public boolean isEmpty() {
        return multimap.isEmpty();
    }

    @Override
    public Set<String> names() {
        return multimap.keySet();
    }

    @Override
    public MultiMap add(String name, String value) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap add(CharSequence name, CharSequence value) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap add(String name, Iterable<String> values) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap add(CharSequence name, Iterable<CharSequence> values) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap addAll(MultiMap map) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap addAll(Map<String, String> headers) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap set(String name, String value) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap set(CharSequence name, CharSequence value) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap set(String name, Iterable<String> values) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap set(CharSequence name, Iterable<CharSequence> values) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap setAll(MultiMap map) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap setAll(Map<String, String> headers) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap remove(String name) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap remove(CharSequence name) {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public MultiMap clear() {
        throw new UnsupportedOperationException("ImmutableListMultimap: operation not supported");
    }

    @Override
    public int size() {
        return multimap.size();
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return multimap.entries().iterator();
    }
}
