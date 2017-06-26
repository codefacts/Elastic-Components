package elasta.criteria;

/**
 * Created by Jango on 2017-01-06.
 */
public interface Main {
    static void main(String[] args) {

        Func and = Ops.and(
            Ops.eq(
                Ops.valueOf("location"),
                Ops.valueOf("Dhaka")
            ),
            Ops.lt(
                Ops.valueOf("height"),
                Ops.valueOf(8)
            ),
            Ops.and(
                Ops.eq(
                    Ops.valueOf("name"),
                    Ops.valueOf("sohan")),
                Ops.lt(
                    Ops.valueOf("salaray"),
                    Ops.valueOf(12000)),
                Ops.or(
                    Ops.not(
                        Ops.gt(
                            Ops.valueOf("age"),
                            Ops.valueOf(12)
                        )
                    ),
                    Ops.ne(
                        Ops.valueOf("size"),
                        Ops.valueOf("34")
                    )
                )
            )
        );

        System.out.println(and.get(String::valueOf));
    }
}
