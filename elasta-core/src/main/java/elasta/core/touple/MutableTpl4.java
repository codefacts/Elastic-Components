package elasta.core.touple;

import elasta.core.intfs.QuadConsumerUnchecked;
import elasta.core.intfs.QuadFunctionUnchecked;
import elasta.core.touple.immutable.Tpl4;
import elasta.core.touple.immutable.Tpls;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpl4<T1, T2, T3, T4> {
    public T1 t1;
    public T2 t2;
    public T3 t3;
    public T4 t4;

    public MutableTpl4() {
    }

    public MutableTpl4(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    public <T5> MutableTpl5<T1, T2, T3, T4, T5> ps(final T5 val) {
        return new MutableTpl5<>(t1, t2, t3, t4, val);
    }

    public MutableTpl3<T1, T2, T3> pp() {
        return new MutableTpl3<>(t1, t2, t3);
    }

    public <T5> MutableTpl5<T1, T2, T3, T4, T5> al(final T5 val) {
        return new MutableTpl5<>(t1, t2, t3, t4, val);
    }

    public <T5> MutableTpl5<T5, T1, T2, T3, T4> af(final T5 val) {
        return new MutableTpl5<>(val, t1, t2, t3, t4);
    }

    public MutableTpl3<T2, T3, T4> df() {
        return new MutableTpl3<>(t2, t3, t4);
    }

    public MutableTpl3<T1, T2, T3> dl() {
        return new MutableTpl3<>(t1, t2, t3);
    }

    public T1 ft() {
        return t1;
    }

    public T4 lt() {
        return t4;
    }

    public Tpl4<T1, T2, T3, T4> toImmutable() {
        return Tpls.of(t1, t2, t3, t4);
    }

    public <R> R apply(final QuadFunctionUnchecked<T1, T2, T3, T4, R> functionUnchecked) {
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

    public void accept(final QuadConsumerUnchecked<T1, T2, T3, T4> consumerUnchecked) {
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

    public <T5, T6> MutableTpl6<T1, T2, T3, T4, T5, T6> jn(final MutableTpl2<T5, T6> MutableTpl2) {
        return new MutableTpl6<>(t1, t2, t3, t4, MutableTpl2.t1, MutableTpl2.t2);
    }

    public <T5, T6, T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> jn(final MutableTpl3<T5, T6, T7> MutableTpl3) {
        return new MutableTpl7<>(t1, t2, t3, t4, MutableTpl3.t1, MutableTpl3.t2, MutableTpl3.t3);
    }

    public <T5, T6, T7, T8> MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final MutableTpl4<T5, T6, T7, T8> MutableTpl4) {
        return new MutableTpl8<>(t1, t2, t3, t4, MutableTpl4.t1, MutableTpl4.t2, MutableTpl4.t3, MutableTpl4.t4);
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

    public T4 getT4() {
        return t4;
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

    public void setT4(T4 t4) {
        this.t4 = t4;
    }
}
