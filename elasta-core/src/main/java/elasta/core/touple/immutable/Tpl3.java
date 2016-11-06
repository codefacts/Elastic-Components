package elasta.core.touple.immutable;

import elasta.core.intfs.Consumer3Unckd;
import elasta.core.intfs.Fun3Unckd;

/**
 * Created by someone on 08/11/2015.
 */
final public class Tpl3<T1, T2, T3> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;

    public Tpl3(final T1 t1, final T2 t2, final T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public <T4> Tpl4<T1, T2, T3, T4> ps(final T4 val) {
        return new Tpl4<>(t1, t2, t3, val);
    }

    public Tpl2<T1, T2> pp() {
        return new Tpl2<>(t1, t2);
    }

    public <T4> Tpl4<T1, T2, T3, T4> al(final T4 val) {
        return new Tpl4<>(t1, t2, t3, val);
    }

    public <T4> Tpl4<T4, T1, T2, T3> af(final T4 val) {
        return new Tpl4<>(val, t1, t2, t3);
    }

    public Tpl2<T2, T3> df() {
        return new Tpl2<>(t2, t3);
    }

    public Tpl2<T1, T2> dl() {
        return new Tpl2<>(t1, t2);
    }

    public T1 ft() {
        return t1;
    }

    public T3 lt() {
        return t3;
    }

    public <R> R apply(final Fun3Unckd<T1, T2, T3, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final Consumer3Unckd<T1, T2, T3> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public <T4, T5> Tpl5<T1, T2, T3, T4, T5> jn(final Tpl2<T4, T5> tpl2) {
        return new Tpl5<>(t1, t2, t3, tpl2.t1, tpl2.t2);
    }

    public <T4, T5, T6> Tpl6<T1, T2, T3, T4, T5, T6> jn(final Tpl3<T4, T5, T6> tpl3) {
        return new Tpl6<>(t1, t2, t3, tpl3.t1, tpl3.t2, tpl3.t3);
    }

    public <T4, T5, T6, T7> Tpl7<T1, T2, T3, T4, T5, T6, T7> jn(final Tpl4<T4, T5, T6, T7> tpl4) {
        return new Tpl7<>(t1, t2, t3, tpl4.t1, tpl4.t2, tpl4.t3, tpl4.t4);
    }

    public <T4, T5, T6, T7, T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final Tpl5<T4, T5, T6, T7, T8> tpl5) {
        return new Tpl8<>(t1, t2, t3, tpl5.t1, tpl5.t2, tpl5.t3, tpl5.t4, tpl5.t5);
    }
}
