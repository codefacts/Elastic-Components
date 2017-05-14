package elasta.composer.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ConvertersMap;
import elasta.pipeline.converter.Converter;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ConvertersMapImpl implements ConvertersMap {
    final Map<Class, Converter> converterMap;

    public ConvertersMapImpl(Map<Class, Converter> converterMap) {
        Objects.requireNonNull(converterMap);
        this.converterMap = ImmutableMap.copyOf(converterMap);
    }

    @Override
    public Map<Class, Converter> getMap() {
        return converterMap;
    }
}
