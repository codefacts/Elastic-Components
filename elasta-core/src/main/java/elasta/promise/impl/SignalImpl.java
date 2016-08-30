package elasta.promise.impl;

import elasta.promise.intfs.Signal;

/**
 * Created by Jango on 8/25/2016.
 */
final public class SignalImpl<T> implements Signal<T> {
    final Object value;
    final Type type;

    SignalImpl(T value, Type type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public boolean isFiltered() {
        return this.type == Signal.Type.FILTERED;
    }

    @Override
    public boolean isSuccess() {
        return this.type == Signal.Type.SUCCESS;
    }

    @Override
    public boolean isError() {
        return this.type == Signal.Type.ERROR;
    }

    public Throwable error() {
        return (type == Type.ERROR) ? (Throwable) value : null;
    }

    public T value() {
        return (type == Type.SUCCESS) ? (T) value : null;
    }

    @Override
    public T orElse(T defaultValue) {
        return (this.value() == null) ? defaultValue : this.value();
    }

    public static <TT> SignalImpl<TT> success(TT value) {
        return new SignalImpl<>(value, Type.SUCCESS);
    }

    public static <TT> SignalImpl<TT> error(Throwable throwable) {
        return new SignalImpl(throwable, Type.ERROR);
    }

    public static <TT> SignalImpl<TT> filtered() {
        return new SignalImpl<>(null, Type.FILTERED);
    }

    @Override
    public String toString() {
        return "Signal: type=" + type + " value=" + value;
    }
}
