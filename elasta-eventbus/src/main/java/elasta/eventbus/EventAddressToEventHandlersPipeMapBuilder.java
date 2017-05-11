package elasta.eventbus;

import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventAddressToEventHandlersPipeMapBuilder {

    EventAddressToEventHandlersPipeMap build(Set<String> addresses, List<MatcherAndHandlerPair> matcherAndHandlerPairs);

    @Value
    final class MatcherAndHandlerPair {
        final EventAddressMatcher eventAddressMatcher;
        final EventHandlerBase eventHandlerBase;

        public MatcherAndHandlerPair(EventAddressMatcher eventAddressMatcher, EventHandlerBase eventHandlerBase) {
            Objects.requireNonNull(eventAddressMatcher);
            Objects.requireNonNull(eventHandlerBase);
            this.eventAddressMatcher = eventAddressMatcher;
            this.eventHandlerBase = eventHandlerBase;
        }
    }
}