package elasta.orm;

import com.google.common.collect.ImmutableList;
import elasta.commons.SimpleCounter;
import elasta.core.touple.MutableTpl4;
import elasta.core.touple.MutableTpls;
import elasta.core.touple.immutable.Tpl2;
import elasta.core.touple.immutable.Tpls;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
                        tableSpec.getColumnSpecs().stream().forEach(
                            columnSpec -> listBuilder.add(
                                Tpls.of(new SelectTuple(aliasFinal, columnSpec.getColumnName()), null)
                            )
                        );
                    } else {

                        final SelectTuple selectTuple = new SelectTuple(alias, column);

                        JoinSpec joinSpec = specMap.get(column);

                        if (joinSpec != null) {

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
                        } else {
                            listBuilder.add(Tpls.of(selectTuple, null));
                        }
                    }
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

        mtpl.t1.delete(mtpl.t1.length() - COMMA_SEP.length(), mtpl.t1.length());

        mtpl.t1.append(" from ");

        for (int i = 0; i < mtpl.t3.value; i++) {
            mtpl.t1.append("(");
        }

        return mtpl.t1.append(mtpl.t2).toString();
    }

    public static void main(String[] args) {

        Map<String, TableSpec> tableSpecMap = createMap().stream().collect(Collectors.toMap(TableSpec::getTableName, tableSpec -> tableSpec));

        Map<String, Map<String, JoinSpec>> tableMap = SqlUtils.toTableMap(tableSpecMap);

        SqlQueryGeneratorImpl generator = new SqlQueryGeneratorImpl(tableMap, tableSpecMap);

        String sql = generator.toSql("br", Arrays.asList(
            "id", "you", "user_id.user_id", "user_id.phone", "user_id.email"
        ));

        System.out.println(sql);
    }

    private static List<TableSpec> createMap() {

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
                        .setColumnName("user_id")
                        .setJavaType(JavaType.STRING)
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
                            .setColumnName("user_id")
                            .setJavaType(JavaType.STRING)
                            .setJoinSpec(
                                new JoinSpecBuilder()
                                    .setJoinColumn("user_id")
                                    .setJoinTable("users")
                                    .setJoinTableAlias("users")
                                    .setJoinType(JoinType.INNER_JOIN)
                                    .createJoinSpec()
                            )
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("house")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("ac")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("region")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setJavaType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("region_name")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("area")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setJavaType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("area_name")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("region_name")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("house")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setJavaType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("house_name")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("area_name")
                            .setJavaType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
        ;

        return builder.build();
    }
}
