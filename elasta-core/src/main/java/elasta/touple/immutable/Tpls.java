package elasta.touple.immutable;

final public class Tpls {

    public static <T1, T2> Tpl2<T1, T2> of(final T1 t1, final T2 t2) {
        return new Tpl2<>(t1, t2);
    }

    public static <T1, T2, T3> Tpl3<T1, T2, T3> of(final T1 t1, final T2 t2, final T3 t3) {
        return new Tpl3<>(t1, t2, t3);
    }

    public static <T1, T2, T3, T4> Tpl4<T1, T2, T3, T4> of(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
        return new Tpl4<>(t1, t2, t3, t4);
    }

    public static <T1, T2, T3, T4, T5> Tpl5<T1, T2, T3, T4, T5> of(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
        return new Tpl5<>(t1, t2, t3, t4, t5);
    }

    public static <T1, T2, T3, T4, T5, T6> Tpl6<T1, T2, T3, T4, T5, T6> of(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6) {
        return new Tpl6<>(t1, t2, t3, t4, t5, t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Tpl7<T1, T2, T3, T4, T5, T6, T7> of(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7) {
        return new Tpl7<>(t1, t2, t3, t4, t5, t6, t7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Tpl8<T1, T2, T3, T4, T5, T6, T7, T8> of(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8) {
        return new Tpl8<>(t1, t2, t3, t4, t5, t6, t7, t8);
    }
}
