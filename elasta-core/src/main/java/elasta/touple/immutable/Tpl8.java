package elasta.touple.immutable;

import elasta.intfs.OctaConsumerUnchecked;
import elasta.intfs.OctaFunctionUnchecked;

/**
 * Created by someone on 08/11/2015.
 */
final public class Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;
    public final T4 t4;
    public final T5 t5;
    public final T6 t6;
    public final T7 t7;
    public final T8 t8;

    public Tpl8(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;
    }

    public Tpl7<T1, T2, T3, T4, T5, T6, T7> pp() {
        return new Tpl7<>(t1, t2, t3, t4, t5, t6, t7);
    }


    public Tpl7<T2, T3, T4, T5, T6, T7, T8> df() {
        return new Tpl7<>(t2, t3, t4, t5, t6, t7, t8);
    }

    public Tpl7<T1, T2, T3, T4, T5, T6, T7> dl() {
        return new Tpl7<>(t1, t2, t3, t4, t5, t6, t7);
    }

    public T1 ft() {
        return t1;
    }

    public T8 lt() {
        return t8;
    }

    public <R> R apply(final OctaFunctionUnchecked<T1, T2, T3, T4, T5, T6, T7, T8, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3, t4, t5, t6, t7, t8);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final OctaConsumerUnchecked<T1, T2, T3, T4, T5, T6, T7, T8> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3, t4, t5, t6, t7, t8);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
