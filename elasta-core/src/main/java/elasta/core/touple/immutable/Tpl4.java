package elasta.core.touple.immutable;

import elasta.core.intfs.Consumer4Unckd;
import elasta.core.intfs.Fun4Unckd;

/**
 * Created by someone on 08/11/2015.
 */
final public class Tpl4<T1, T2, T3, T4> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;
    public final T4 t4;

    public Tpl4(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    public <T5> Tpl5<T1, T2, T3, T4, T5> ps(final T5 val) {
        return new Tpl5<>(t1, t2, t3, t4, val);
    }

    public Tpl3<T1, T2, T3> pp() {
        return new Tpl3<>(t1, t2, t3);
    }

    public <T5> Tpl5<T1, T2, T3, T4, T5> al(final T5 val) {
        return new Tpl5<>(t1, t2, t3, t4, val);
    }

    public <T5> Tpl5<T5, T1, T2, T3, T4> af(final T5 val) {
        return new Tpl5<>(val, t1, t2, t3, t4);
    }

    public Tpl3<T2, T3, T4> df() {
        return new Tpl3<>(t2, t3, t4);
    }

    public Tpl3<T1, T2, T3> dl() {
        return new Tpl3<>(t1, t2, t3);
    }

    public T1 ft() {
        return t1;
    }

    public T4 lt() {
        return t4;
    }

    public <R> R apply(final Fun4Unckd<T1, T2, T3, T4, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3, t4);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final Consumer4Unckd<T1, T2, T3, T4> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3, t4);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public <T5, T6> Tpl6<T1, T2, T3, T4, T5, T6> jn(final Tpl2<T5, T6> tpl2) {
        return new Tpl6<>(t1, t2, t3, t4, tpl2.t1, tpl2.t2);
    }

    public <T5, T6, T7> Tpl7<T1, T2, T3, T4, T5, T6, T7> jn(final Tpl3<T5, T6, T7> tpl3) {
        return new Tpl7<>(t1, t2, t3, t4, tpl3.t1, tpl3.t2, tpl3.t3);
    }

    public <T5, T6, T7, T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final Tpl4<T5, T6, T7, T8> tpl4) {
        return new Tpl8<>(t1, t2, t3, t4, tpl4.t1, tpl4.t2, tpl4.t3, tpl4.t4);
    }
}
