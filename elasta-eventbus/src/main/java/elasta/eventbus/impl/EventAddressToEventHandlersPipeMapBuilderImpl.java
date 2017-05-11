package elasta.eventbus.impl;

import com.google.common.collect.*;
import elasta.eventbus.*;

import java.util.*;

/**
 * Created by sohan on 5/9/2017.
 */
final public class EventAddressToEventHandlersPipeMapBuilderImpl implements EventAddressToEventHandlersPipeMapBuilder {

    @Override
    public EventAddressToEventHandlersPipeMap build(Set<String> addresses, List<MatcherAndHandlerPair> matcherAndHandlerPairs) {
        return buildEventBus(addresses, matcherAndHandlerPairs);
    }

    private EventAddressToEventHandlersPipeMap buildEventBus(Set<String> addresses, List<MatcherAndHandlerPair> matcherAndHandlerPairs) {

        ImmutableListMultimap.Builder<String, EventHandlerBase> builder = ImmutableListMultimap.builder();

        addresses.forEach(address -> matcherAndHandlerPairs.forEach(matcherAndHandlerPair -> {

            if (matcherAndHandlerPair.getEventAddressMatcher().matches(address)) {
                builder.put(address, EventBusUtils.toProcessorP(matcherAndHandlerPair.getEventHandlerBase()));
            }

        }));

        return new EventAddressToEventHandlersPipeMapImpl(
            toMap(builder.build())
        );
    }

    private Map<String, EventHandlersPipe> toMap(ImmutableListMultimap<String, EventHandlerBase> multimap) {

        ImmutableMap.Builder<String, EventHandlersPipe> builder = ImmutableMap.builder();

        multimap.keySet().forEach(address -> builder.put(address, new EventHandlersPipeImpl(multimap.get(address))));

        return builder.build();
    }
}
