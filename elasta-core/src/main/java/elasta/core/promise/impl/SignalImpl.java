package elasta.core.promise.impl;

import elasta.core.promise.intfs.Signal;

/**
 * Created by Jango on 8/25/2016.
 */
final public class SignalImpl<T> implements Signal<T> {
    final Object value;
    final Type type;

    private SignalImpl(T value, Type type) {
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

    public Throwable err() {
        return (type == Type.ERROR) ? ((ErrorAndLastValue) value).err() : null;
    }

    @Override
    public <P> P lastValue() {
        return (type == Type.ERROR) ? ((ErrorAndLastValue) value).lastVal() : null;
    }

    public T val() {
        return (type == Type.SUCCESS) ? (T) value : null;
    }

    @Override
    public T orElse(T defaultValue) {
        return (this.val() == null) ? defaultValue : this.val();
    }

    public static <TT> SignalImpl<TT> success(TT value) {
        return new SignalImpl<>(value, Type.SUCCESS);
    }

    public static <TT> SignalImpl<TT> error(Throwable throwable) {
        return new SignalImpl(new ErrorAndLastValue(throwable, null), Type.ERROR);
    }

    public static <TT> SignalImpl<TT> error(Throwable throwable, Object lastValue) {
        return new SignalImpl(new ErrorAndLastValue(throwable, lastValue), Type.ERROR);
    }

    public static <TT> SignalImpl<TT> filtered() {
        return new SignalImpl<>(null, Type.FILTERED);
    }

    @Override
    public String toString() {
        return "Signal: type=" + type + " value=" + value;
    }

    /**
     * Created by Jango on 11/9/2016.
     */
    public static class ErrorAndLastValue {
        private final Throwable error;
        private final Object lastValue;

        public ErrorAndLastValue(Throwable error, Object lastValue) {
            this.error = error;
            this.lastValue = lastValue;
        }

        public Throwable err() {
            return error;
        }

        public <T> T lastVal() {
            return (T) lastValue;
        }
    }
}
