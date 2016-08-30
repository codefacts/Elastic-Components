package elasta.core.touple.immutable;

import elasta.core.intfs.PetaFunctionUnchecked;
import elasta.core.intfs.PetaConsumerUnchecked;

/**
 * Created by someone on 08/11/2015.
 */
final public class Tpl5<T1, T2, T3, T4, T5> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;
    public final T4 t4;
    public final T5 t5;

    public Tpl5(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
    }

    public <T6> Tpl6<T1, T2, T3, T4, T5, T6> ps(final T6 val) {
        return new Tpl6<>(t1, t2, t3, t4, t5, val);
    }

    public Tpl4<T1, T2, T3, T4> pp() {
        return new Tpl4<>(t1, t2, t3, t4);
    }

    public <T6> Tpl6<T1, T2, T3, T4, T5, T6> al(final T6 val) {
        return new Tpl6<>(t1, t2, t3, t4, t5, val);
    }

    public <T6> Tpl6<T6, T1, T2, T3, T4, T5> af(final T6 val) {
        return new Tpl6<>(val, t1, t2, t3, t4, t5);
    }

    public Tpl4<T2, T3, T4, T5> df() {
        return new Tpl4<>(t2, t3, t4, t5);
    }

    public Tpl4<T1, T2, T3, T4> dl() {
        return new Tpl4<>(t1, t2, t3, t4);
    }

    public T1 ft() {
        return t1;
    }

    public T5 lt() {
        return t5;
    }

    public <R> R apply(final PetaFunctionUnchecked<T1, T2, T3, T4, T5, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3, t4, t5);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final PetaConsumerUnchecked<T1, T2, T3, T4, T5> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3, t4, t5);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public <T6, T7> Tpl7<T1, T2, T3, T4, T5, T6, T7> jn(final Tpl2<T6, T7> tpl2) {
        return new Tpl7<>(t1, t2, t3, t4, t5, tpl2.t1, tpl2.t2);
    }

    public <T6, T7, T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final Tpl3<T6, T7, T8> tpl3) {
        return new Tpl8<>(t1, t2, t3, t4, t5, tpl3.t1, tpl3.t2, tpl3.t3);
    }
}
