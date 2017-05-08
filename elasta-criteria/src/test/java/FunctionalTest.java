import com.google.common.collect.ImmutableList;
import elasta.criteria.Func;
import elasta.criteria.funcs.ops.ArrayOps;
import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.LogicalOps;
import elasta.criteria.funcs.ops.ValueHolderOps;
import elasta.criteria.funcs.ops.impl.*;

/**
 * Created by sohan on 5/8/2017.
 */
public interface FunctionalTest {
    static void main(String[] asfd) {
        final LogicalOps logicalOps = new LogicalOpsImpl();

        final ValueHolderOps valueHolderOps = new ValueHolderOpsImpl();

        final ComparisionOps comparisionOps = new ComparisionOpsImpl();

        final ArrayOpsImpl arrayOps = new ArrayOpsImpl();
        final FunctionalOpsImpl functionalOps = new FunctionalOpsImpl();

        Func and = functionalOps.count(
            functionalOps.distinct(
                valueHolderOps.valueOf("id")
            )
        );

        System.out.println(and.get(String::valueOf));

        Func in = arrayOps.in(
            valueHolderOps.valueOf("id"),
            new Func[]{
                valueHolderOps.valueOf(1),
                valueHolderOps.valueOf(2),
                valueHolderOps.valueOf(3),
                valueHolderOps.valueOf(4),
                valueHolderOps.valueOf(5)
            }
        );

        System.out.println(in.get(String::valueOf));
    }
}
