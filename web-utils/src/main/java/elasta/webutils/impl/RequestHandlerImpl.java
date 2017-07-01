package elasta.webutils.impl;

import elasta.eventbus.SimpleEventBus;
import elasta.webutils.*;
import elasta.webutils.model.UriAndHttpMethodPair;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
final public class RequestHandlerImpl implements RequestHandler {
    private final RequestConverter requestConverter;
    private final UriToEventAddressTranslator uriToEventAddressTranslator;
    private final ResponseGenerator responseGenerator;
    private final SimpleEventBus eventBus;

    public RequestHandlerImpl(
        RequestConverter requestConverter,
        UriToEventAddressTranslator uriToEventAddressTranslator,
        ResponseGenerator responseGenerator,
        SimpleEventBus eventBus
    ) {
        Objects.requireNonNull(requestConverter);
        Objects.requireNonNull(uriToEventAddressTranslator);
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(eventBus);

        this.requestConverter = requestConverter;
        this.uriToEventAddressTranslator = uriToEventAddressTranslator;
        this.responseGenerator = responseGenerator;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RoutingContext context) {

        try {

            final String eventAddress = uriToEventAddressTranslator.apply(
                UriAndHttpMethodPair.builder()
                    .uri(context.request().uri())
                    .httpMethod(context.request().method())
                    .build()
            );

            final Object value = requestConverter.apply(context);

            eventBus.<JsonObject>sendAndReceiveJsonObject(
                SimpleEventBus.Params.builder()
                    .address(eventAddress)
                    .message(value)
                    .build()
            )
                .then(message -> responseGenerator.reply(message, context))
                .err(context::fail)
            ;

        } catch (Throwable throwable) {
            context.fail(throwable);
        }
    }
}
