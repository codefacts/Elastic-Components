package elasta.composer.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import elasta.composer.ContextHolder;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class ContextHolderImpl implements ContextHolder {
    ListMultimap<String, String> contextMap;

    @Override
    public ContextHolder setMap(ListMultimap<String, String> contextMap) {
        Objects.requireNonNull(contextMap);
        this.contextMap = contextMap;
        return this;
    }

    @Override
    public ListMultimap<String, String> getMap() {
        Objects.requireNonNull(contextMap);
        return contextMap;
    }
}
