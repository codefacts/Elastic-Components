package elasta.orm;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jango on 9/15/2016.
 */
public class SelectClauseGeneratorImpl implements SelectClauseGenerator {
    private final Map<String, Map<String, JoinSpec>> tableMap;
    private final Map<String, TableSpec> tableSpecMap;

    public SelectClauseGeneratorImpl(Map<String, Map<String, JoinSpec>> tableMap, Map<String, TableSpec> tableSpecMap) {
        this.tableMap = tableMap;
        this.tableSpecMap = tableSpecMap;
    }

    @Override
    public List<SelectTuple> toSelectClause(String rootTable, String rootTableAlias, List<String> fields) {
        return fields.stream().map(s -> s.split("\\."))
            .flatMap(columns -> {
                ImmutableList.Builder<SelectTuple> listBuilder = ImmutableList.builder();

                Map<String, JoinSpec> specMap = tableMap.get(rootTable);
                TableSpec tableSpec = tableSpecMap.get(rootTable);

                String alias = rootTableAlias;
                for (String column : columns) {

                    if (column.equals("*")) {
                        final String aliasFinal = alias;
                        tableSpec.getColumnSpecs().stream().map(columnSpec -> listBuilder.add(new SelectTuple(aliasFinal, columnSpec.getColumnName())));
                    }

                    listBuilder.add(new SelectTuple(alias, column));

                    JoinSpec joinSpec = specMap.get(column);
                    alias = joinSpec.getJoinTableAlias();

                    specMap = tableMap.get(joinSpec.getJoinTable());
                    tableSpec = tableSpecMap.get(joinSpec.getJoinTable());
                }

                return listBuilder.build().stream();
            }).collect(Collectors.toList());
    }
}
