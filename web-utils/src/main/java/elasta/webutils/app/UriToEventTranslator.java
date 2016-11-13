package elasta.webutils.app;

import elasta.core.intfs.Fun1Unckd;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;

/**
 * Created by Jango on 11/6/2016.
 */
public interface UriToEventTranslator<T> extends Fun1Unckd<UriToEventTranslator.RequestInfo<T>, String> {

    class RequestInfo<T> {
        final String uri;
        final String absoluteUri;
        final HttpMethod httpMethod;
        final MultiMap headers;
        final T value;

        public RequestInfo(String uri, String absoluteUri, HttpMethod httpMethod, MultiMap headers, T value) {
            this.uri = uri;
            this.absoluteUri = absoluteUri;
            this.httpMethod = httpMethod;
            this.headers = headers;
            this.value = value;
        }

        public String getUri() {
            return uri;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public MultiMap getHeaders() {
            return headers;
        }

        public T getValue() {
            return value;
        }
    }
}
