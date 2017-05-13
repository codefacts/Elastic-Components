package elasta.composer.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import elasta.commons.Utils;
import elasta.composer.ContextHolder;
import elasta.composer.RequestContext;
import elasta.composer.ex.AppContextException;
import elasta.pipeline.converter.Converter;

import java.util.*;

/**
 * Created by sohan on 5/12/2017.
 */
final public class RequestContextImpl implements RequestContext {
    final ContextHolder contextHolder;
    final Map<Class, Converter> convertersMap;

    public RequestContextImpl(ContextHolder contextHolder, Map<Class, Converter> convertersMap) {
        Objects.requireNonNull(contextHolder);
        Objects.requireNonNull(convertersMap);
        this.contextHolder = contextHolder;
        this.convertersMap = ImmutableMap.copyOf(
            checkConvertersMap(convertersMap)
        );
    }

    @Override
    public boolean containsKey(String key) {
        return contextHolder.getMap().containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return contextHolder.getMap().keySet();
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return getString(key).map(s -> convert(Integer.class, s));
    }

    @Override
    public Optional<Long> getLong(String key) {
        return getString(key).map(s -> convert(Long.class, s));
    }

    @Override
    public Optional<Float> getFloat(String key) {
        return getString(key).map(s -> convert(Float.class, s));
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return getString(key).map(s -> convert(Double.class, s));
    }

    @Override
    public Optional<String> getString(String key) {
        return getValue(key);
    }

    @Override
    public Optional<Date> getDate(String key) {
        return getString(key).map(s -> convert(Date.class, s));
    }

    @Override
    public List<String> getAll(String key) {
        return contextHolder.getMap().get(key);
    }

    @Override
    public ListMultimap<String, String> getMultimap() {
        return contextHolder.getMap();
    }

    private <T> T convert(Class<T> tClass, String value) {
        return (T) convertersMap.get(tClass).convert(value);
    }

    private Optional<String> getValue(String key) {

        List<String> strings = contextHolder.getMap().get(key);

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
