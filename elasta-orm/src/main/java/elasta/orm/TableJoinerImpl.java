package elasta.orm;

import elasta.commons.Utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static elasta.commons.Utils.or;

/**
 * Created by Jango on 9/15/2016.
 */
public class TableJoinerImpl implements TableJoiner {
    final Map<String, JoinSpec> joinSpecsByColumnMap;

    public TableJoinerImpl(Map<String, JoinSpec> joinSpecsByColumnMap) {
        this.joinSpecsByColumnMap = joinSpecsByColumnMap;
    }

    @Override
    public List<JoinTuple> join(String rootAlias, List<TableJoinerSpec> tableJoinerSpecs) {

        return
            tableJoinerSpecs.stream()
                .map(
                    tableJoinerSpec -> {

                        final String column = tableJoinerSpec.getColumn();

                        JoinSpec joinSpec = joinSpecsByColumnMap
                            .get(column);

                        return new JoinTupleBuilder()
                            .setAlias(rootAlias)
                            .setColumn(column)
                            .setJoinTable(
                                joinSpec.getJoinTable()
                            )
                            .setJoinTableAlias(or(tableJoinerSpec.getJoinTableAlias(), joinSpec.getJoinTableAlias()))
                            .setJoinColumn(joinSpec.getJoinColumn())
                            .setJoinType(or(tableJoinerSpec.getJoinType(), joinSpec.getJoinType()))
                            .createJoinTuple();
                    }
                ).collect(Collectors.toList());
    }
}
