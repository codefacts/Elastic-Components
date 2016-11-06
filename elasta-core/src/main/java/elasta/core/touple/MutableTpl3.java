package elasta.core.touple;

import elasta.core.intfs.Consumer3Unckd;
import elasta.core.intfs.Fun3Unckd;
import elasta.core.touple.immutable.Tpl3;
import elasta.core.touple.immutable.Tpls;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpl3<T1, T2, T3> {
    public T1 t1;
    public T2 t2;
    public T3 t3;

    public MutableTpl3() {
    }

    public MutableTpl3(final T1 t1, final T2 t2, final T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public <T4> MutableTpl4<T1, T2, T3, T4> ps(final T4 val) {
        return new MutableTpl4<>(t1, t2, t3, val);
    }

    public MutableTpl2<T1, T2> pp() {
        return new MutableTpl2<>(t1, t2);
    }

    public <T4> MutableTpl4<T1, T2, T3, T4> al(final T4 val) {
        return new MutableTpl4<>(t1, t2, t3, val);
    }

    public <T4> MutableTpl4<T4, T1, T2, T3> af(final T4 val) {
        return new MutableTpl4<>(val, t1, t2, t3);
    }

    public MutableTpl2<T2, T3> df() {
        return new MutableTpl2<>(t2, t3);
    }

    public MutableTpl2<T1, T2> dl() {
        return new MutableTpl2<>(t1, t2);
    }

    public T1 ft() {
        return t1;
    }

    public T3 lt() {
        return t3;
    }

    public Tpl3<T1, T2, T3> toImmutable() {
        return Tpls.of(t1, t2, t3);
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

    public <T4, T5> MutableTpl5<T1, T2, T3, T4, T5> jn(final MutableTpl2<T4, T5> MutableTpl2) {
        return new MutableTpl5<>(t1, t2, t3, MutableTpl2.t1, MutableTpl2.t2);
    }

    public <T4, T5, T6> MutableTpl6<T1, T2, T3, T4, T5, T6> jn(final MutableTpl3<T4, T5, T6> MutableTpl3) {
        return new MutableTpl6<>(t1, t2, t3, MutableTpl3.t1, MutableTpl3.t2, MutableTpl3.t3);
    }

    public <T4, T5, T6, T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> jn(final MutableTpl4<T4, T5, T6, T7> MutableTpl4) {
        return new MutableTpl7<>(t1, t2, t3, MutableTpl4.t1, MutableTpl4.t2, MutableTpl4.t3, MutableTpl4.t4);
    }

    public <T4, T5, T6, T7, T8> MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final MutableTpl5<T4, T5, T6, T7, T8> MutableTpl5) {
        return new MutableTpl8<>(t1, t2, t3, MutableTpl5.t1, MutableTpl5.t2, MutableTpl5.t3, MutableTpl5.t4, MutableTpl5.t5);
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }

    public T3 getT3() {
        return t3;
    }

    public void setT1(T1 t1) {
        this.t1 = t1;
    }

    public void setT2(T2 t2) {
        this.t2 = t2;
    }

    public void setT3(T3 t3) {
        this.t3 = t3;
    }
}
