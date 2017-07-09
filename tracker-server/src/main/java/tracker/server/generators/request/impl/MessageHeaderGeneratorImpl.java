package tracker.server.generators.request.impl;

import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.ext.web.RoutingContext;
import tracker.server.generators.request.MessageHeaderGenerator;

import java.util.Objects;

/**
 * Created by sohan on 7/8/2017.
 */
final public class MessageHeaderGeneratorImpl implements MessageHeaderGenerator {
    final String headerPrefix;

    public MessageHeaderGeneratorImpl(String headerPrefix) {
        Objects.requireNonNull(headerPrefix);
        this.headerPrefix = headerPrefix;
    }

    @Override
    public MultiMap generate(RoutingContext context) {

        CaseInsensitiveHeaders headers = new CaseInsensitiveHeaders();

        context.request().headers().entries().stream()
            .filter(entry -> entry.getKey().startsWith(headerPrefix))
            .forEach(entry -> headers.set(entry.getKey().substring(headerPrefix.length()), entry.getValue()));

        return headers;
    }
}
