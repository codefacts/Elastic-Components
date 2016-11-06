package elasta.core.eventbus.impl;

import elasta.core.eventbus.Context;
import elasta.core.promise.intfs.Promise;
import elasta.core.utils.TypedMap;
import elasta.core.utils.TypedMapImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Jango on 11/6/2016.
 */
public class ContextImpl implements Context {
    private final String event;
    private final Map params;
    private final Action1 onNext;

    public ContextImpl(String event, Map<String, ?> params, Action1 onNext) {
        this.event = event;
        this.params = params;
        this.onNext = onNext;
    }

    @Override
    public String event() {
        return event;
    }

    @Override
    public TypedMap params() {
        return new TypedMapImpl(params);
    }

    @Override
    public Promise next(Object o) {
        return onNext.call(o);
    }

    public interface Action1 {
        Promise call(Object o);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContextImpl context = (ContextImpl) o;

        if (event != null ? !event.equals(context.event) : context.event != null) return false;
        if (params != null ? !params.equals(context.params) : context.params != null) return false;
        return onNext != null ? onNext.equals(context.onNext) : context.onNext == null;

    }

    @Override
    public int hashCode() {
        int result = event != null ? event.hashCode() : 0;
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (onNext != null ? onNext.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ContextImpl{" +
            "event='" + event + '\'' +
            ", params=" + params +
            '}';
    }
}
