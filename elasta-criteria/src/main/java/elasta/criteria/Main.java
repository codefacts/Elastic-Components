package elasta.criteria;

import elasta.criteria.funcs.ops.ComparisionOps;import elasta.criteria.funcs.ops.LogicalOps;import elasta.criteria.funcs.ops.ValueHolderOps;import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.LogicalOps;
import elasta.criteria.funcs.ops.ValueHolderOps;
import elasta.criteria.funcs.ops.impl.ComparisionOpsImpl;
import elasta.criteria.funcs.ops.impl.LogicalOpsImpl;
import elasta.criteria.funcs.ops.impl.ValueHolderOpsImpl;

/**
 * Created by Jango on 2017-01-06.
 */
public interface Main {
    static void main(String[] args) {
        LogicalOps logicalOps = new LogicalOpsImpl();

        ValueHolderOps valueHolderOps = new ValueHolderOpsImpl();

        ComparisionOps comparisionOps = new ComparisionOpsImpl();

        Func and = logicalOps.and(
            comparisionOps.eq(
                valueHolderOps.valueOf("location"),
                valueHolderOps.valueOf("Dhaka")
            ),
            comparisionOps.lt(
                valueHolderOps.valueOf("height"),
                valueHolderOps.valueOf(8)
            ),
            logicalOps.and(
                comparisionOps.eq(
                    valueHolderOps.valueOf("name"),
                    valueHolderOps.valueOf("sohan")),
                comparisionOps.lt(
                    valueHolderOps.valueOf("salaray"),
                    valueHolderOps.valueOf(12000)),
                logicalOps.or(
                    logicalOps.not(
                        comparisionOps.gt(
                            valueHolderOps.valueOf("age"),
                            valueHolderOps.valueOf(12)
                        )
                    ),
                    comparisionOps.ne(
                        valueHolderOps.valueOf("size"),
                        valueHolderOps.valueOf("34")
                    )
                )
            )
        );

        System.out.println(and.get(String::valueOf));
    }
}
