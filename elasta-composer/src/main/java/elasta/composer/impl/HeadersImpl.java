package elasta.composer.impl;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import elasta.commons.Utils;
import elasta.composer.Headers;
import elasta.composer.ex.AppContextException;
import elasta.pipeline.converter.Converter;

import java.util.*;

/**
 * Created by sohan on 5/12/2017.
 */
final public class HeadersImpl implements Headers {
    final ListMultimap<String, String> multimap;
    final Map<Class, Converter> convertersMap;

    public HeadersImpl(ListMultimap<String, String> multimap, Map<Class, Converter> convertersMap) {
        Objects.requireNonNull(multimap);
        Objects.requireNonNull(convertersMap);
        this.multimap = multimap;
        this.convertersMap = ImmutableMap.copyOf(
            checkConvertersMap(convertersMap)
        );
    }

    @Override
    public boolean containsKey(String key) {
        return multimap.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return multimap.keySet();
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return get(key).map(s -> convert(Integer.class, s));
    }

    @Override
    public Optional<Long> getLong(String key) {
        return get(key).map(s -> convert(Long.class, s));
    }

    @Override
    public Optional<Float> getFloat(String key) {
        return get(key).map(s -> convert(Float.class, s));
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return get(key).map(s -> convert(Double.class, s));
    }

    @Override
    public Optional<String> get(String key) {
        return getValue(key);
    }

    @Override
    public Optional<Date> getDate(String key) {
        return get(key).map(s -> convert(Date.class, s));
    }

    @Override
    public List<String> getAll(String key) {
        return multimap.get(key);
    }

    @Override
    public ListMultimap<String, String> getMultimap() {
        return multimap;
    }

    @Override
    public Headers addAll(ListMultimap<String, String> multimap) {
        return new HeadersImpl(
            ImmutableListMultimap.<String, String>builder().putAll(multimap).build(),
            convertersMap
        );
    }

    @Override
    public Headers addAll(Map<String, String> map) {
        return new HeadersImpl(
            ImmutableListMultimap.<String, String>builder().putAll(
                map.entrySet()
            ).build(),
            convertersMap
        );
    }

    public Map<Class, Converter> getConvertersMap() {
        return convertersMap;
    }

    private <T> T convert(Class<T> tClass, String value) {
        return (T) convertersMap.get(tClass).convert(value);
    }

    private Optional<String> getValue(String key) {

        List<String> strings = multimap.get(key);

        if (strings.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(strings.get(0));
    }

    private Map<Class, Converter> checkConvertersMap(Map<Class, Converter> convertersMap) {
        ImmutableSet.of(
            Integer.class, Long.class, Float.class, Double.class, Boolean.class, Date.class
        ).forEach(aClass -> {
            if (Utils.not(convertersMap.containsKey(aClass))) {
                throw new AppContextException("ConvertersMap does not contains converter for type '" + aClass + "'");
            }
        });
        return convertersMap;
    }
}
