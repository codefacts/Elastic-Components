package elasta.orm;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jango on 9/15/2016.
 */
public class TableJoinerRecursiveImpl implements TableJoinerRecursive {
    private final Map<String, Map<String, JoinSpec>> tableMap;
    private final TableJoinerFactory tableJoinerFactory;

    public TableJoinerRecursiveImpl(Map<String, Map<String, JoinSpec>> tableMap, TableJoinerFactory tableJoinerFactory) {
        this.tableMap = tableMap;
        this.tableJoinerFactory = tableJoinerFactory;
    }

    @Override
    public List<JoinTuple> joinRecursive(String rootTable, List<List<String>> joinColumns) {

        return joinColumns.stream().flatMap(columns -> {

            Map<String, JoinSpec> joinSpecMap = tableMap.get(rootTable);

            ImmutableList.Builder<JoinTuple> listBuilder = ImmutableList.builder();

            for (String column : columns) {

                JoinSpec joinSpec = joinSpecMap.get(column);

                JoinTuple joinTuple = new JoinTupleBuilder()
                    .setColumn(column)
                    .setAlias(rootTable)
                    .setJoinTable(joinSpec.getJoinTable())
                    .setJoinTableAlias(joinSpec.getJoinTable())
                    .setJoinColumn(joinSpec.getJoinColumn())
                    .setJoinType(joinSpec.getJoinType())
                    .createJoinTuple();

                listBuilder.add(joinTuple);

                joinSpecMap = tableMap.get(joinSpec.getJoinTable());

            }

            return listBuilder.build().stream();

        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {

    }
}
