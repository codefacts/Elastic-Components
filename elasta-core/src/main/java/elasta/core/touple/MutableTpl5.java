package elasta.core.touple;

import elasta.core.intfs.PetaConsumerUnchecked;
import elasta.core.intfs.PetaFunctionUnchecked;
import elasta.core.touple.immutable.Tpl5;
import elasta.core.touple.immutable.Tpls;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpl5<T1, T2, T3, T4, T5> {
    public T1 t1;
    public T2 t2;
    public T3 t3;
    public T4 t4;
    public T5 t5;

    public MutableTpl5() {
    }

    public MutableTpl5(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
    }

    public <T6> MutableTpl6<T1, T2, T3, T4, T5, T6> ps(final T6 val) {
        return new MutableTpl6<>(t1, t2, t3, t4, t5, val);
    }

    public MutableTpl4<T1, T2, T3, T4> pp() {
        return new MutableTpl4<>(t1, t2, t3, t4);
    }

    public <T6> MutableTpl6<T1, T2, T3, T4, T5, T6> al(final T6 val) {
        return new MutableTpl6<>(t1, t2, t3, t4, t5, val);
    }

    public <T6> MutableTpl6<T6, T1, T2, T3, T4, T5> af(final T6 val) {
        return new MutableTpl6<>(val, t1, t2, t3, t4, t5);
    }

    public MutableTpl4<T2, T3, T4, T5> df() {
        return new MutableTpl4<>(t2, t3, t4, t5);
    }

    public MutableTpl4<T1, T2, T3, T4> dl() {
        return new MutableTpl4<>(t1, t2, t3, t4);
    }

    public T1 ft() {
        return t1;
    }

    public T5 lt() {
        return t5;
    }

    public Tpl5<T1, T2, T3, T4, T5> toImmutable() {
        return Tpls.of(t1, t2, t3, t4, t5);
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

    public <T6, T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> jn(final MutableTpl2<T6, T7> MutableTpl2) {
        return new MutableTpl7<>(t1, t2, t3, t4, t5, MutableTpl2.t1, MutableTpl2.t2);
    }

    public <T6, T7, T8> MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> jn(final MutableTpl3<T6, T7, T8> MutableTpl3) {
        return new MutableTpl8<>(t1, t2, t3, t4, t5, MutableTpl3.t1, MutableTpl3.t2, MutableTpl3.t3);
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
}
