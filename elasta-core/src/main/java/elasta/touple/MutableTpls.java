package elasta.touple;

/**
 * Created by someone on 08/11/2015.
 */
final public class MutableTpls {

    public static <T1, T2> MutableTpl2<T1, T2> of(T1 t1, T2 t2) {
        return new MutableTpl2<>(t1, t2);
    }

    public static <T1, T2, T3> MutableTpl3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new MutableTpl3<>(t1, t2, t3);
    }

    public static <T1, T2, T3, T4> MutableTpl4<T1, T2, T3, T4> of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new MutableTpl4<T1, T2, T3, T4>(t1, t2, t3, t4);
    }

    public static <T1, T2, T3, T4, T5> MutableTpl5<T1, T2, T3, T4, T5> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new MutableTpl5<T1, T2, T3, T4, T5>(t1, t2, t3, t4, t5);
    }

    public static <T1, T2, T3, T4, T5, T6> MutableTpl6<T1, T2, T3, T4, T5, T6> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6
            t6) {
        return new MutableTpl6<T1, T2, T3, T4, T5, T6>(t1, t2, t3, t4, t5, t6);
    }


    public static <T1, T2, T3, T4, T5, T6, T7> MutableTpl7<T1, T2, T3, T4, T5, T6, T7> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5,
                                                                                          T6 t6, T7 t7) {
        return new MutableTpl7<T1, T2, T3, T4, T5, T6, T7>(t1, t2, t3, t4, t5, t6, t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> of(T1 t1, T2 t2, T3 t3, T4
            t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        return new MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8>(t1, t2, t3, t4, t5, t6, t7, t8);
    }
}
