package elasta.touple;

import elasta.intfs.HexaConsumerUnchecked;
import elasta.intfs.HexaFunctionUnchecked;
import elasta.touple.immutable.Tpl6;
import elasta.touple.immutable.Tpls;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpl6<T1, T2, T3, T4, T5, T6> {
    public T1 t1;
    public T2 t2;
    public T3 t3;
    public T4 t4;
    public T5 t5;
    public T6 t6;

    public MutableTpl6() {
    }

    public MutableTpl6(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
    }

    public <T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> ps(final T7 val) {
        return new MutableTpl7<>(t1, t2, t3, t4, t5, t6, val);
    }

    public MutableTpl5<T1, T2, T3, T4, T5> pp() {
        return new MutableTpl5<>(t1, t2, t3, t4, t5);
    }

    public <T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> al(final T7 val) {
        return new MutableTpl7<>(t1, t2, t3, t4, t5, t6, val);
    }

    public <T7> MutableTpl7<T7, T1, T2, T3, T4, T5, T6> af(final T7 val) {
        return new MutableTpl7<>(val, t1, t2, t3, t4, t5, t6);
    }

    public MutableTpl5<T2, T3, T4, T5, T6> df() {
        return new MutableTpl5<>(t2, t3, t4, t5, t6);
    }

    public MutableTpl5<T1, T2, T3, T4, T5> dl() {
        return new MutableTpl5<>(t1, t2, t3, t4, t5);
    }

    public T1 ft() {
        return t1;
    }

    public T6 lt() {
        return t6;
    }

    public Tpl6<T1, T2, T3, T4, T5, T6> toImmutable() {
        return Tpls.of(t1, t2, t3, t4, t5, t6);
    }

    public <R> R apply(final HexaFunctionUnchecked<T1, T2, T3, T4, T5, T6, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3, t4, t5, t6);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final HexaConsumerUnchecked<T1, T2, T3, T4, T5, T6> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3, t4, t5, t6);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public <T7, T8> MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final MutableTpl2<T7, T8> MutableTpl2) {
        return new MutableTpl8<>(t1, t2, t3, t4, t5, t6, MutableTpl2.t1, MutableTpl2.t2);
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

    public T5 getT5() {
        return t5;
    }

    public T6 getT6() {
        return t6;
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

    public void setT5(T5 t5) {
        this.t5 = t5;
    }

    public void setT6(T6 t6) {
        this.t6 = t6;
    }
}
