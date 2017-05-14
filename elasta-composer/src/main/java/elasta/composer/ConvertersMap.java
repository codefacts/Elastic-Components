package elasta.composer;

import elasta.pipeline.converter.Converter;

import java.util.Map;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ConvertersMap {
    Map<Class, Converter> getMap();
}
