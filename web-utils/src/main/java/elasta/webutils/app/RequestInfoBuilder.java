package elasta.webutils.app;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;

public class RequestInfoBuilder<T> {
    private String uri;
    private HttpMethod httpMethod;
    private MultiMap headers;
    private T value;
    private String absoluteUri;

    public RequestInfoBuilder<T> setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RequestInfoBuilder<T> setAbsoluteUri(String absoluteUri) {
        this.absoluteUri = absoluteUri;
        return this;
    }

    public RequestInfoBuilder<T> setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public RequestInfoBuilder<T> setHeaders(MultiMap headers) {
        this.headers = headers;
        return this;
    }

    public RequestInfoBuilder<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public UriToEventTranslator.RequestInfo<T> createRequestInfo() {
        return new UriToEventTranslator.RequestInfo<>(uri, absoluteUri, httpMethod, headers, value);
    }
}