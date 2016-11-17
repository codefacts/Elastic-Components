package elasta.webutils.app.impl;

import elasta.core.eventbus.SimpleEventBus;
import elasta.webutils.app.*;
import elasta.webutils.app.exceptions.WebException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/6/2016.
 */
public class RequestHandlerImpl implements RequestHandler {
    private final RequestConverter<JsonObject> converter;
    private final UriToEventTranslator<JsonObject> uriToEventTranslator;
    private final ResponseGenerator<JsonObject> responseGenerator;
    private final SimpleEventBus eventBus;

    public RequestHandlerImpl(
        RequestConverter converter,
        UriToEventTranslator uriToEventTranslator,
        ResponseGenerator responseGenerator,
        SimpleEventBus eventBus) {

        this.converter = converter;
        this.uriToEventTranslator = uriToEventTranslator;
        this.responseGenerator = responseGenerator;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RoutingContext context) {

        try {

            JsonObject val = converter.apply(context);

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
