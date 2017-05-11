package elasta.eventbus;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.eventbus.impl.RegisterEventHandlersPipeHelper;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventBusUtils {

    List<Function<EventHandlerBase, ProcessorP>> FUNCTIONS = ImmutableList.of(
        base -> toProcessorP((EventHandler) base),
        base -> toProcessorP((EventHandlerP) base),

        base -> toProcessorP((Filter) base),
        base -> toProcessorP((FilterP) base),

        base -> toProcessorP((Processor) base),
        base -> (ProcessorP) base
    );

    static ProcessorP toProcessorP(EventHandlerBase base) {
        return FUNCTIONS.get(base.code()).apply(base);
    }

    static <T, R> ProcessorP<T, R> toProcessorP(Processor<T, R> processor) {
        return message -> Promises.callable(() -> processor.apply(message));
    }

    static <T> ProcessorP<T, T> toProcessorP(Filter<T> filter) {
        return message -> Promises.filter(message, filter::test);
    }

    static <T> ProcessorP<T, T> toProcessorP(FilterP<T> filterP) {
        return message -> callFilterP(message, filterP);
    }

    static <T> ProcessorP<T, T> toProcessorP(EventHandler<T> handler) {
        return message -> Promises.runnable(() -> handler.accept(message)).map(aVoid -> message);
    }

    static <T> ProcessorP<T, T> toProcessorP(EventHandlerP<T> handlerP) {
        return message -> callHandlerP(message, handlerP);
    }

    static <T> Promise<Message<T>> callFilterP(Message<T> message, FilterP<T> filterP) {
        return filterP.test(message).filter(pass -> pass).map(aBoolean -> message);
    }

    static <T> Promise<Message<T>> callHandlerP(Message<T> message, EventHandlerP<T> handlerP) {
        return handlerP.apply(message).map(o -> message);
    }

    static void registerEventHandlersPipeMap(RegisterHandlersParams params) {
        new RegisterEventHandlersPipeHelper().register(params);
    }

    static void registerLocaleEventHandlersPipeMap(RegisterHandlersParams params) {
        new RegisterEventHandlersPipeHelper().registerLocale(params);
    }

    @Value
    @Builder
    final class RegisterHandlersParams {
        final EventBus eventBus;
        final EventAddressToEventHandlersPipeMap eventAddressToEventHandlersPipeMap;
        final EventBusErrorHandler errorHandler;

        public RegisterHandlersParams(EventBus eventBus, EventAddressToEventHandlersPipeMap eventAddressToEventHandlersPipeMap, EventBusErrorHandler errorHandler) {
            Objects.requireNonNull(eventBus);
            Objects.requireNonNull(eventAddressToEventHandlersPipeMap);
            Objects.requireNonNull(errorHandler);
            this.eventBus = eventBus;
            this.eventAddressToEventHandlersPipeMap = eventAddressToEventHandlersPipeMap;
            this.errorHandler = errorHandler;
        }
    }
}
