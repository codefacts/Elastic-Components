package elasta.core.touple.immutable;

import elasta.core.intfs.Consumer2Unckd;
import elasta.core.intfs.Fun2Unckd;

/**
 * Created by someone on 08/11/2015.
 */
final public class Tpl2<T1, T2> {
    public final T1 t1;
    public final T2 t2;

    public Tpl2(final T1 t1, final T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public <T3> Tpl3<T1, T2, T3> ps(final T3 val) {
        return new Tpl3<>(t1, t2, val);
    }

    public T1 pp() {
        return t1;
    }

    public <T3> Tpl3<T1, T2, T3> al(final T3 val) {
        return new Tpl3<>(t1, t2, val);
    }

    public <T3> Tpl3<T3, T1, T2> af(final T3 val) {
        return new Tpl3<>(val, t1, t2);
    }

    public T2 df() {
        return t2;
    }

    public T1 dl() {
        return t1;
    }

    public T1 ft() {
        return t1;
    }

    public T2 lt() {
        return t2;
    }

    public <R> R apply(final Fun2Unckd<T1, T2, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final Consumer2Unckd<T1, T2> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public <T3, T4> Tpl4<T1, T2, T3, T4> jn(final Tpl2<T3, T4> tpl2) {
        return new Tpl4<>(t1, t2, tpl2.t1, tpl2.t2);
    }

    public <T3, T4, T5> Tpl5<T1, T2, T3, T4, T5> jn(final Tpl3<T3, T4, T5> tpl3) {
        return new Tpl5<>(t1, t2, tpl3.t1, tpl3.t2, tpl3.t3);
    }

    public <T3, T4, T5, T6> Tpl6<T1, T2, T3, T4, T5, T6> jn(final Tpl4<T3, T4, T5, T6> tpl4) {
        return new Tpl6<>(t1, t2, tpl4.t1, tpl4.t2, tpl4.t3, tpl4.t4);
    }

    public <T3, T4, T5, T6, T7> Tpl7<T1, T2, T3, T4, T5, T6, T7> jn(final Tpl5<T3, T4, T5, T6, T7> tpl5) {
        return new Tpl7<>(t1, t2, tpl5.t1, tpl5.t2, tpl5.t3, tpl5.t4, tpl5.t5);
    }

    public <T3, T4, T5, T6, T7, T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final Tpl6<T3, T4, T5, T6, T7, T8> tpl6) {
        return new Tpl8<>(t1, t2, tpl6.t1, tpl6.t2, tpl6.t3, tpl6.t4, tpl6.t5, tpl6.t6);
    }
}
