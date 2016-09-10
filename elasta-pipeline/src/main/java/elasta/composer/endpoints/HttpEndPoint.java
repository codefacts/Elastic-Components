package elasta.composer.endpoints;

import elasta.composer.util.ExceptionUtil;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/3/16.
 */
final public class HttpEndPoint implements Endpoint<Buffer> {
    private final HttpClient httpClient;
    public static final String METHOD = "method";
    public static final String URI = "uri";

    public HttpEndPoint(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private String value(Object val) {
        if (val instanceof JsonArray) {
            return ((JsonArray) val).encode();
        } else if (val instanceof JsonObject) {
            return ((JsonObject) val).encode();
        } else return val.toString();
    }

    @Override
    public void process(Message<Buffer> message) {
        Defer<Buffer> defer = Promises.<Buffer>defer();

        final MultiMap headers = message.headers();

        try {
            HttpClientRequest request = httpClient.requestAbs(
                HttpMethod.valueOf(headers.get(METHOD)),
                headers.get(URI),
                response -> response.exceptionHandler(defer::reject)
                    .bodyHandler(defer::resolve));

            headers.remove(METHOD).remove(URI);

            if (headers.size() > 0) {
                request.sendHead();
                headers.forEach(e -> request.putHeader(e.getKey(), e.getValue()));
            }

            request
                .write(message.body())
                .exceptionHandler(defer::reject)
                .end();
        } catch (Exception ex) {
            defer.reject(ex);
        }

        defer.promise()
            .then(buffer -> message.reply(buffer))
            .err(e -> ExceptionUtil.fail(message, e));
    }
}
