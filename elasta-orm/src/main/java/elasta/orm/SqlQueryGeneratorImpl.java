package elasta.orm;

import com.google.common.collect.ImmutableList;
import elasta.commons.SimpleCounter;
import elasta.core.touple.MutableTpl4;
import elasta.core.touple.MutableTpls;
import elasta.core.touple.immutable.Tpl2;
import elasta.core.touple.immutable.Tpls;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/15/2016.
 */
public class SqlQueryGeneratorImpl implements SqlQueryGenerator {
    private static final String COMMA_SEP = ", ";
    private final Map<String, Map<String, JoinSpec>> tableMap;
    private final Map<String, TableSpec> tableSpecMap;

    public SqlQueryGeneratorImpl(Map<String, Map<String, JoinSpec>> tableMap, Map<String, TableSpec> tableSpecMap) {
        this.tableMap = tableMap;
        this.tableSpecMap = tableSpecMap;
    }

    @Override
    public String toSql(String rootTable, List<String> fields) {

        String rootTableAlias = "root";

        MutableTpl4<StringBuilder, StringBuilder, SimpleCounter, String> mtpl = fields
            .stream().map(s -> s.split("\\."))
            .flatMap(columns -> {

                ImmutableList.Builder<Tpl2<SelectTuple, JoinTuple>> listBuilder = ImmutableList.builder();

                Map<String, JoinSpec> specMap = tableMap.get(rootTable);
                TableSpec tableSpec = tableSpecMap.get(rootTable);

                String alias = rootTableAlias;

                for (String column : columns) {

                    if (column.equals("*")) {
                        final String aliasFinal = alias;
                        tableSpec.getColumnSpecs().stream().map(
                            columnSpec -> listBuilder.add(
                                Tpls.of(new SelectTuple(aliasFinal, columnSpec.getColumnName()), null)
                            )
                        );
                    }

                    final SelectTuple selectTuple = new SelectTuple(alias, column);

                    JoinSpec joinSpec = specMap.get(column);

                    JoinTuple joinTuple = new JoinTupleBuilder()
                        .setColumn(column)
                        .setAlias(rootTableAlias)
                        .setJoinTable(joinSpec.getJoinTable())
                        .setJoinTableAlias(joinSpec.getJoinTableAlias())
                        .setJoinColumn(joinSpec.getJoinColumn())
                        .setJoinType(joinSpec.getJoinType())
                        .createJoinTuple();

                    listBuilder.add(Tpls.of(selectTuple, joinTuple));

                    alias = joinSpec.getJoinTableAlias();

                    specMap = tableMap.get(joinSpec.getJoinTable());
                    tableSpec = tableSpecMap.get(joinSpec.getJoinTable());

                }

                return listBuilder.build().stream();
            })
            .reduce(
                MutableTpls.of(new StringBuilder("select "), new StringBuilder(rootTable + " " + rootTableAlias), new SimpleCounter(), rootTableAlias),
                (tpl4, tpl2) -> {
                    tpl4.t1.append(tpl2.t1.getAlias()).append(".").append(tpl2.t1.getField()).append(COMMA_SEP);
                    if (tpl2.t2 != null) {
                        tpl4.t3.value++;

                        JoinTuple joinTuple = tpl2.t2;
                        StringBuilder stringBuilder = tpl4.t2;

                        String joinTableAlias = joinTuple.getJoinTableAlias();
                        String joinTable = joinTuple.getJoinTable();

                        stringBuilder.append(" ").append(joinTuple.getJoinType()).append(" ");

                        if (joinTableAlias.equalsIgnoreCase(joinTable)) {

                            stringBuilder.append(joinTable);
                        } else {

                            stringBuilder.append(joinTable).append(" ").append(joinTableAlias);
                        }

                        String prevAlias = tpl4.t4;

                        stringBuilder
                            .append(" on ").append(prevAlias).append(".").append(joinTuple.getColumn())
                            .append(" = ").append(joinTableAlias).append(".").append(joinTuple.getJoinColumn())
                            .append(")");
                        tpl4.t4 = joinTableAlias;
                    }

                    return tpl4;
                }, (u, u2) -> u);

        return mtpl.t1.append(mtpl.t2).toString();
    }

    public static void main(String[] args) {

        ImmutableList.Builder<TableSpec> builder = ImmutableList.builder();
        builder.add(
            new TableSpecBuilder()
                .setTableName("users")
                .setPrimaryKey("id")
                .setColumnSpecs(ImmutableList.of(
                    new ColumnSpecBuilder()
                        .setColumnName("id")
                        .setJavaType(JavaType.LONG)
                        .createColumnSpec(),
                    new ColumnSpecBuilder()
                        .setColumnName("name")
                        .setJavaType(JavaType.STRING)
                        .createColumnSpec(),
                    new ColumnSpecBuilder()
                        .setColumnName("email")
                        .setJavaType(JavaType.STRING)
                        .createColumnSpec(),
                    new ColumnSpecBuilder()
                        .setColumnName("phone")
                        .setJavaType(JavaType.STRING)
                        .createColumnSpec()
                ))
                .createTableSpec()
        )
            .add(
                new TableSpecBuilder()
                    .setTableName("br")
                    .setPrimaryKey("id")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setJavaType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("name")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("email")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("phone")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
        ;
    }
}
