package elasta.touple;

import elasta.intfs.OctaConsumerUnchecked;
import elasta.intfs.OctaFunctionUnchecked;
import elasta.touple.immutable.Tpl8;
import elasta.touple.immutable.Tpls;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> {
    public T1 t1;
    public T2 t2;
    public T3 t3;
    public T4 t4;
    public T5 t5;
    public T6 t6;
    public T7 t7;
    public T8 t8;

    public MutableTpl8() {
    }

    public MutableTpl8(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;
    }

    public MutableTpl7<T1, T2, T3, T4, T5, T6, T7> pp() {
        return new MutableTpl7<>(t1, t2, t3, t4, t5, t6, t7);
    }


    public MutableTpl7<T2, T3, T4, T5, T6, T7, T8> df() {
        return new MutableTpl7<>(t2, t3, t4, t5, t6, t7, t8);
    }

    public MutableTpl7<T1, T2, T3, T4, T5, T6, T7> dl() {
        return new MutableTpl7<>(t1, t2, t3, t4, t5, t6, t7);
    }

    public T1 ft() {
        return t1;
    }

    public T8 lt() {
        return t8;
    }

    public Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> toImmutable() {
        return Tpls.of(t1, t2, t3, t4, t5, t6, t7, t8);
    }

    public <R> R apply(final OctaFunctionUnchecked<T1, T2, T3, T4, T5, T6, T7, T8, R> functionUnchecked) {
        try {
            return functionUnchecked.apply(t1, t2, t3, t4, t5, t6, t7, t8);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public void accept(final OctaConsumerUnchecked<T1, T2, T3, T4, T5, T6, T7, T8> consumerUnchecked) {
        try {
            consumerUnchecked.accept(t1, t2, t3, t4, t5, t6, t7, t8);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
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

    public T7 getT7() {
        return t7;
    }

    public T8 getT8() {
        return t8;
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

    public void setT7(T7 t7) {
        this.t7 = t7;
    }

    public void setT8(T8 t8) {
        this.t8 = t8;
    }
}
