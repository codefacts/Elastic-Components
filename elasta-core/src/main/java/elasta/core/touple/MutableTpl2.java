package elasta.core.touple;

import elasta.core.intfs.BiConsumerUnchecked;
import elasta.core.intfs.BiFunctionUnchecked;
import elasta.core.touple.immutable.Tpl2;
import elasta.core.touple.immutable.Tpls;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpl2<T1, T2> {
    public T1 t1;
    public T2 t2;

    public MutableTpl2() {
    }

    public MutableTpl2(final T1 t1, final T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public <T3> MutableTpl3<T1, T2, T3> ps(final T3 val) {
        return new MutableTpl3<>(t1, t2, val);
    }

    public MutableTpl1<T1> pp() {
        return new MutableTpl1<>(t1);
    }

    public <T3> MutableTpl3<T1, T2, T3> al(final T3 val) {
        return new MutableTpl3<>(t1, t2, val);
    }

    public <T3> MutableTpl3<T3, T1, T2> af(final T3 val) {
        return new MutableTpl3<>(val, t1, t2);
    }

    public MutableTpl1<T2> df() {
        return new MutableTpl1<>(t2);
    }

    public MutableTpl1<T1> dl() {
        return new MutableTpl1<>(t1);
    }

    public T1 ft() {
        return t1;
    }

    public T2 lt() {
        return t2;
    }

    public Tpl2<T1, T2> toImmutable() {
        return Tpls.of(t1, t2);
    }

    public <R> R apply(final BiFunctionUnchecked<T1, T2, R> functionUnchecked) {
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

    public void accept(final BiConsumerUnchecked<T1, T2> consumerUnchecked) {
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

    public <T3, T4> MutableTpl4<T1, T2, T3, T4> jn(final MutableTpl2<T3, T4> MutableTpl2) {
        return new MutableTpl4<>(t1, t2, MutableTpl2.t1, MutableTpl2.t2);
    }

    public <T3, T4, T5> MutableTpl5<T1, T2, T3, T4, T5> jn(final MutableTpl3<T3, T4, T5> MutableTpl3) {
        return new MutableTpl5<>(t1, t2, MutableTpl3.t1, MutableTpl3.t2, MutableTpl3.t3);
    }

    public <T3, T4, T5, T6> MutableTpl6<T1, T2, T3, T4, T5, T6> jn(final MutableTpl4<T3, T4, T5, T6> MutableTpl4) {
        return new MutableTpl6<>(t1, t2, MutableTpl4.t1, MutableTpl4.t2, MutableTpl4.t3, MutableTpl4.t4);
    }

    public <T3, T4, T5, T6, T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> jn(final MutableTpl5<T3, T4, T5, T6, T7> MutableTpl5) {
        return new MutableTpl7<>(t1, t2, MutableTpl5.t1, MutableTpl5.t2, MutableTpl5.t3, MutableTpl5.t4, MutableTpl5.t5);
    }

    public <T3, T4, T5, T6, T7, T8> MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final MutableTpl6<T3, T4, T5, T6, T7, T8> MutableTpl6) {
        return new MutableTpl8<>(t1, t2, MutableTpl6.t1, MutableTpl6.t2, MutableTpl6.t3, MutableTpl6.t4, MutableTpl6.t5, MutableTpl6.t6);
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public void setT1(T1 t1) {
        this.t1 = t1;
    }

    public void setT2(T2 t2) {
        this.t2 = t2;
    }
}
