import elasta.criteria.Func;
import elasta.criteria.Ops;

/**
 * Created by sohan on 5/8/2017.
 */
public interface FunctionalTest {
    static void main(String[] asfd) {

        Func and = Ops.count(
            Ops.distinct(
                Ops.valueOf("id")
            )
        );

        System.out.println(and.get(String::valueOf));

        Func in = Ops.in(
            Ops.valueOf("id"),
            new Func[]{
                Ops.valueOf(1),
                Ops.valueOf(2),
                Ops.valueOf(3),
                Ops.valueOf(4),
                Ops.valueOf(5)
            }
        );

        System.out.println(in.get(String::valueOf));
    }
}
