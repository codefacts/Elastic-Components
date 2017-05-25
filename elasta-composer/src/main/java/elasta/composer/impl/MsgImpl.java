package elasta.composer.impl;

import com.google.common.collect.ListMultimap;
import elasta.commons.Utils;
import elasta.composer.Context;
import elasta.composer.Headers;
import elasta.composer.Msg;
import elasta.composer.RequestBuilder;
import elasta.composer.ex.NoUserInContextException;
import elasta.composer.model.request.UserModel;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/15/2017.
 */
final public class MsgImpl<T> implements Msg<T> {
    final Headers headers;
    final Context context;
    final T body;
    final Object userId;

    private MsgImpl(Headers headers, Context context, T body, Object userId) {
        Objects.requireNonNull(headers);
        Objects.requireNonNull(context);
        Objects.requireNonNull(body);
        Objects.requireNonNull(userId);
        checkHeadersContainsUserId(headers);
        this.headers = headers;
        this.context = context;
        this.body = body;
        this.userId = userId;
    }

    @Override
    public Headers headers() {
        return headers;
    }

    @Override
    public Context context() {
        return context;
    }

    @Override
    public T body() {
        return body;
    }

    @Override
    public <R> R userId() {
        return cast(userId);
    }

    @Override
    public Msg<T> addHeaders(ListMultimap<String, String> multimap) {
        return new MsgImpl<T>(
            this.headers.addAll(multimap),
            this.context,
            this.body,
            this.userId
        );
    }

    @Override
    public Msg<T> addHeaders(Map<String, String> map) {
        return new MsgImpl<T>(
            headers.addAll(map),
            context,
            body,
            userId
        );
    }

    @Override
    public Msg<T> addContext(Map<String, Object> map) {
        return new MsgImpl<T>(
            headers,
            context.addAll(map),
            body,
            userId
        );
    }

    @Override
    public <R> Msg<R> withBody(R body) {
        return new MsgImpl<R>(
            headers,
            context,
            body,
            userId
        );
    }

    private void checkHeadersContainsUserId(Headers headers) {
        if (Utils.not(headers.get(UserModel.userId).isPresent())) {
            throw new NoUserInContextException("No '" + UserModel.userId + "' found in headers");
        }
    }

    private <R> R cast(Object userId) {
        return (R) userId;
    }

    final public static class RequestImplBuilder<T> implements RequestBuilder<T> {
        private Headers headers;
        private Context context;
        private T body;
        private Object userId;

        public RequestImplBuilder() {
        }

        public RequestImplBuilder(Msg<T> msg) {
            headers = msg.headers();
            context = msg.context();
            body = msg.body();
            userId = msg.userId();
        }

        @Override
        public RequestImplBuilder<T> headers(Headers headers) {
            this.headers = headers;
            return this;
        }

        @Override
        public RequestImplBuilder<T> context(Context context) {
            this.context = context;
            return this;
        }

        @Override
        public RequestImplBuilder<T> body(T body) {
            this.body = body;
            return this;
        }

        @Override
        public RequestBuilder<T> userId(Object userId) {
            this.userId = userId;
            return this;
        }

        @Override
        public MsgImpl<T> build() {
            return new MsgImpl<T>(headers, context, body, userId);
        }
    }
}
