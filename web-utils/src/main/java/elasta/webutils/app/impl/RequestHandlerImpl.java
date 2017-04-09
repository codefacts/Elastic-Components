package elasta.webutils.app.impl;

import elasta.core.eventbus.SimpleEventBus;
import elasta.webutils.app.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by Jango on 11/6/2016.
 */
public class RequestHandlerImpl implements RequestHandler {
    private final JsonObjectRequestConverter jsonObjectRequestConverter;
    private final UriToEventTranslator<JsonObject> uriToEventTranslator;
    private final ResponseGenerator<JsonObject> responseGenerator;
    private final SimpleEventBus eventBus;

    public RequestHandlerImpl(
        JsonObjectRequestConverter jsonObjectRequestConverter,
        UriToEventTranslator<JsonObject> uriToEventTranslator,
        ResponseGenerator<JsonObject> responseGenerator,
        SimpleEventBus eventBus) {
        Objects.requireNonNull(jsonObjectRequestConverter);
        Objects.requireNonNull(uriToEventTranslator);
        Objects.requireNonNull(responseGenerator);

        this.jsonObjectRequestConverter = jsonObjectRequestConverter;
        this.uriToEventTranslator = uriToEventTranslator;
        this.responseGenerator = responseGenerator;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RoutingContext context) {

        try {

            JsonObject val = jsonObjectRequestConverter.apply(context);

            String eventAddress = uriToEventTranslator.apply(context, val);

            eventBus.<JsonObject>fire(eventAddress, val)
                .then(jsonObject -> responseGenerator.reply(jsonObject, context))
                .err(context::fail)
            ;

        } catch (Throwable throwable) {
            context.fail(throwable);
        }
    }
}
