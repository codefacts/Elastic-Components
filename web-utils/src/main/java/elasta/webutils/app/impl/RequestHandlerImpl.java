package elasta.webutils.app.impl;

import elasta.core.eventbus.SimpleEventBus;
import elasta.webutils.app.RequestConverter;
import elasta.webutils.app.RequestHandler;
import elasta.webutils.app.ResponseGenerator;
import elasta.webutils.app.UriToEventTranslator;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/6/2016.
 */
public class RequestHandlerImpl implements RequestHandler {
    private final RequestConverter<JsonObject> converter;
    private final UriToEventTranslator uriToEventTranslator;
    private final ResponseGenerator responseGenerator;
    private final SimpleEventBus eventBus;

    public RequestHandlerImpl(RequestConverter converter, UriToEventTranslator uriToEventTranslator, ResponseGenerator responseGenerator, SimpleEventBus eventBus) {
        this.converter = converter;
        this.uriToEventTranslator = uriToEventTranslator;
        this.responseGenerator = responseGenerator;
        this.eventBus = eventBus;
    }

    @Override
    public void handle(RoutingContext context) {

        try {

            HttpServerRequest request = context.request();

            JsonObject val = converter.apply(context);

            String eventAddress = uriToEventTranslator.apply(request.uri(), request.method());

            eventBus.<JsonObject>fire(eventAddress, val)
                .then(jsonObject -> responseGenerator.reply(jsonObject, context))
                .err(context::fail)
            ;

        } catch (Throwable throwable) {
            context.fail(throwable);
        }
    }
}
