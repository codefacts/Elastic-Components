package elasta.webutils.impl;

import elasta.eventbus.SimpleEventBus;
import elasta.webutils.*;
import elasta.webutils.model.UriAndHttpMethodPair;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
final public class JsonArrayRequestHandlerImpl implements JsonArrayRequestHandler {
    private final RequestHandler requestHandler;

    public JsonArrayRequestHandlerImpl(
        JsonArrayRequestConverter jsonArrayRequestConverter,
        UriToEventAddressTranslator uriToEventAddressTranslator,
        ResponseGenerator responseGenerator,
        SimpleEventBus eventBus
    ) {
        this.requestHandler = new RequestHandlerImpl(
            jsonArrayRequestConverter,
            uriToEventAddressTranslator,
            responseGenerator,
            eventBus
        );
    }

    @Override
    public void handle(RoutingContext context) {
        requestHandler.handle(context);
    }
}
