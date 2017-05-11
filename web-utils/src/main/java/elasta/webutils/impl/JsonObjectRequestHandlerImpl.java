package elasta.webutils.impl;

import elasta.eventbus.SimpleEventBus;
import elasta.webutils.JsonObjectRequestConverter;
import elasta.webutils.UriToEventAddressTranslator;
import elasta.webutils.*;
import elasta.webutils.model.UriAndHttpMethodPair;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by Jango on 11/6/2016.
 */
final public class JsonObjectRequestHandlerImpl implements JsonObjectRequestHandler {
    final RequestHandler requestHandler;

    public JsonObjectRequestHandlerImpl(
        JsonObjectRequestConverter jsonObjectRequestConverter,
        UriToEventAddressTranslator uriToEventAddressTranslator,
        ResponseGenerator responseGenerator,
        SimpleEventBus eventBus
    ) {
        this.requestHandler = new RequestHandlerImpl(
            jsonObjectRequestConverter,
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
