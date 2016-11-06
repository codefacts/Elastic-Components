package elasta.core.touple.immutable;

import elasta.core.intfs.Consumer7Unckd;
import elasta.core.intfs.Fun7Unckd;

/**
 * Created by someone on 08/11/2015.
 */
final public class Tpl7<T1, T2, T3, T4, T5, T6, T7> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;
    public final T4 t4;
    public final T5 t5;
    public final T6 t6;
    public final T7 t7;

    public Tpl7(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
    }

    public <T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> ps(final T8 val) {
        return new Tpl8<>(t1, t2, t3, t4, t5, t6, t7, val);
    }

    public Tpl6<T1, T2, T3, T4, T5, T6> pp() {
        return new Tpl6<>(t1, t2, t3, t4, t5, t6);
    }

    public <T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> al(final T8 val) {
        return new Tpl8<>(t1, t2, t3, t4, t5, t6, t7, val);
    }

    public <T8> Tpl8<T8, T1, T2, T3, T4, T5, T6, T7> af(final T8 val) {
        return new Tpl8<>(val, t1, t2, t3, t4, t5, t6, t7);
    }

    public Tpl6<T2, T3, T4, T5, T6, T7> df() {
        return new Tpl6<>(t2, t3, t4, t5, t6, t7);
    }

    public Tpl6<T1, T2, T3, T4, T5, T6> dl() {
        return new Tpl6<>(t1, t2, t3, t4, t5, t6);
    }

    public T1 ft() {
        return t1;
    }

    public T7 lt() {
        return t7;
    }

    public <R> R apply(final Fun7Unckd<T1, T2, T3, T4, T5, T6, T7, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3, t4, t5, t6, t7);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final Consumer7Unckd<T1, T2, T3, T4, T5, T6, T7> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3, t4, t5, t6, t7);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
