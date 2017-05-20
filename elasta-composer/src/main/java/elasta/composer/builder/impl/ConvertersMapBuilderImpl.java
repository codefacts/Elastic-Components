package elasta.composer.builder.impl;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ConvertersMap;
import elasta.composer.Headers;
import elasta.composer.builder.ConvertersMapBuilder;
import elasta.composer.impl.ConvertersMapImpl;
import elasta.composer.impl.HeadersImpl;
import elasta.pipeline.converter.Converter;
import elasta.pipeline.converter.Converters;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.Map;

/**
 * Created by sohan on 5/20/2017.
 */
final public class ConvertersMapBuilderImpl implements ConvertersMapBuilder {
    @Override
    public ConvertersMap build() {
        return new ConvertersMapImpl(
            converterMap()
        );
    }

    private Map<Class, Converter> converterMap() {
        return ImmutableMap.<Class, Converter>builder()
            .putAll(
                ImmutableMap.of(
                    String.class, o -> o,
                    Integer.class, Converters::toInteger,
                    Long.class, Converters::toLong
                )
            )
            .putAll(
                ImmutableMap.of(
                    Boolean.class, Converters::toBoolean,
                    Float.class, Converters::toFloat,
                    Double.class, Converters::toDouble,
                    Date.class, Converters::toDate
                )
            )
            .build();
    }

    public static void main(String[] asdf) {
        final ConvertersMap convertersMap = new ConvertersMapBuilderImpl().build();
        new HeadersImpl(
            ImmutableListMultimap.of(),
            convertersMap.getMap()
        );
    }
}
