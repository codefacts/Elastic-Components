package elasta.orm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;

import java.util.List;
import java.util.Map;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 9/15/2016.
 */
public class SqlQueryGeneratorImpl implements SqlQueryGenerator {
    private static final String DOT = ".";
    private static final String COMMA = ", ";
    private static final String ROOT = "root";
    private final Map<String, TableSpec> tableSpecMap;
    private final Map<String, Map<String, ColumnSpec>> columnSpecMapByNameMap;
    private final Map<String, Map<String, ColumnSpec>> columnSpecMapByColumnMap;

    public SqlQueryGeneratorImpl(Map<String, TableSpec> tableSpecMap, Map<String, Map<String, ColumnSpec>> columnSpecMapByNameMap, Map<String, Map<String, ColumnSpec>> columnSpecMapByColumnMap) {
        this.tableSpecMap = tableSpecMap;
        this.columnSpecMapByNameMap = columnSpecMapByNameMap;
        this.columnSpecMapByColumnMap = columnSpecMapByColumnMap;
    }

    @Override
    public String toSql(String rootTable, List<SqlField> fields) {
        List<TblPathFields> fieldsList = translate(rootTable, fields);
        String select = sqlSelect(rootTable, fieldsList);
        String from = sqlJoin(rootTable, fieldsList);
        return "select " + select + " from " + from;
    }

    private String sqlJoin(String rootTable, List<TblPathFields> fields) {

        StringBuilder sqlBuilder = new StringBuilder().append(rootTable).append(" ").append(ROOT).append(" ");
        int count = 0;

        if (fields == null || fields.isEmpty()) {
            count += joinAllFieldsRecursive(tableSpecMap.get(rootTable).getColumnSpecs(), ROOT, sqlBuilder);
        }

        for (int i = 0; i < fields.size(); i++) {

            TblPathFields ff = fields.get(i);

            if (ff.path != null && not(ff.path.isEmpty())) {
                count += joinPath(ff.path, rootTable, sqlBuilder);
            }

            if (ff.fields == null || ff.fields.isEmpty()) {

                count += joinAllFieldsRecursive(tableSpecMap.get(ff.table).getColumnSpecs(), ff.alias, sqlBuilder);

            } else if (ff.fields.get(0).equals("**")) {
                List<ColumnSpec> columnSpecs = tableSpecMap.get(ff.table).getColumnSpecs();

                for (ColumnSpec columnSpec : columnSpecs) {
                    JoinSpec joinSpec = columnSpec.getJoinSpec();
                    if (joinSpec != null) {
                        join(joinSpec, ff.alias, columnSpec.getColumnName(), sqlBuilder);
                        count++;
                    }
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < count; j++) {
            stringBuilder.append("(");
        }
        stringBuilder.append(sqlBuilder);

        return stringBuilder.toString();
    }

    private int joinPath(List<String> path, String rootTable, StringBuilder sqlBuilder) {
        Map<String, ColumnSpec> specMap = columnSpecMapByColumnMap.get(rootTable);
        String alias = ROOT;
        for (String joinField : path) {
            ColumnSpec columnSpec = specMap.get(joinField);
            JoinSpec joinSpec = columnSpec.getJoinSpec();

            join(joinSpec, alias, columnSpec.getColumnName(), sqlBuilder);

            specMap = columnSpecMapByColumnMap.get(joinSpec.getJoinTable());
            alias = joinSpec.getJoinTableAlias();
        }
        return path.size();
    }

    private void join(JoinSpec joinSpec, String alias, String columnName, StringBuilder sqlBuilder) {

        sqlBuilder.append(joinSpec.getJoinType()).append(" ").append(joinSpec.getJoinTable()).append(" ")
            .append(
                joinSpec.getJoinTable().equalsIgnoreCase(joinSpec.getJoinTableAlias()) ? "" : joinSpec.getJoinTableAlias()
            )
            .append(" on ").append(alias).append(".").append(columnName)
            .append(" = ").append(joinSpec.getJoinTableAlias()).append(".").append(joinSpec.getJoinColumn()).append(") ");
    }

    private int joinAllFieldsRecursive(List<ColumnSpec> columnSpecs, String alias, StringBuilder sqlBuilder) {
        int count = 0;
        for (ColumnSpec columnSpec : columnSpecs) {
            JoinSpec joinSpec = columnSpec.getJoinSpec();
            if (joinSpec != null) {

                join(joinSpec, alias, columnSpec.getColumnName(), sqlBuilder);

                count++;
                count += joinAllFieldsRecursive(tableSpecMap.get(joinSpec.getJoinTable()).getColumnSpecs(), joinSpec.getJoinTableAlias(), sqlBuilder);
            }
        }
        return count;
    }

    private List<TblPathFields> translate(String table, List<SqlField> fields) {
        ImmutableList.Builder<TblPathFields> listBuilder = ImmutableList.builder();

        fields.forEach(sqlField -> {
            TableAndPath tableAndPath = joinTableName(table, sqlField.getPath());
            String tableName = tableAndPath.table;
            Map<String, ColumnSpec> columnSpecMap = columnSpecMapByNameMap.get(tableName);

            ImmutableList.Builder<String> lBuilder = ImmutableList.builder();
            if (sqlField.getFields() != null) {
                sqlField.getFields().forEach(name -> {
                    ColumnSpec columnSpec = columnSpecMap.get(name);
                    lBuilder.add(columnSpec == null ? name : columnSpec.getColumnName());
                });
            }

            listBuilder.add(new TblPathFields(
                tableAndPath.table,
                tableAndPath.alias, tableAndPath.path,
                lBuilder.build()
            ));
        });

        return listBuilder.build();
    }

    private TableAndPath joinTableName(String rootTable, String path) {

        if (path == null || ROOT.equalsIgnoreCase(path) || rootTable.equalsIgnoreCase(path)) {
            return new TableAndPath(rootTable, ROOT, null);
        } else {

            String[] joinFields = path.split("\\.");

            ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
            String joinTable = rootTable;
            String alias = ROOT;
            Map<String, ColumnSpec> specMap = columnSpecMapByNameMap.get(joinTable);
            for (int i = 0; i < joinFields.length; i++) {
                String fieldName = joinFields[i];

                ColumnSpec columnSpec = specMap.get(fieldName);

                JoinSpec joinSpec = columnSpec.getJoinSpec();

                joinTable = joinSpec.getJoinTable();

                alias = joinSpec.getJoinTableAlias();

                listBuilder.add(columnSpec.getColumnName());

                specMap = columnSpecMapByNameMap.get(joinTable);

            }

            return new TableAndPath(joinTable, alias, listBuilder.build());
        }
    }

    private String sqlSelect(String rootTable, List<TblPathFields> fields) {

        StringBuilder sqlBuilder = new StringBuilder();

        if (fields == null || fields.isEmpty()) {
            addAllFieldsRecursive(tableSpecMap.get(rootTable).getColumnSpecs(), ROOT, sqlBuilder);
        }

        for (int i = 0; i < fields.size(); i++) {

            TblPathFields ff = fields.get(i);

            if (ff.fields == null || ff.fields.isEmpty()) {

                addAllFieldsRecursive(tableSpecMap.get(ff.table).getColumnSpecs(), ff.alias, sqlBuilder);

            } else if (ff.fields.get(0).equals("*")) {

                List<ColumnSpec> columnSpecs = tableSpecMap.get(ff.table).getColumnSpecs();
                addFields(columnSpecs, ff.alias, sqlBuilder);

            } else if (ff.fields.get(0).equals("**")) {

                List<ColumnSpec> columnSpecs = tableSpecMap.get(ff.table).getColumnSpecs();
                for (ColumnSpec columnSpec : columnSpecs) {
                    sqlBuilder.append(ff.alias + "." + columnSpec.getColumnName() + COMMA);
                    JoinSpec joinSpec = columnSpec.getJoinSpec();
                    if (joinSpec != null) {

                        List<ColumnSpec> colSpecsJonTable = tableSpecMap.get(joinSpec.getJoinTable()).getColumnSpecs();
                        addFields(colSpecsJonTable, joinSpec.getJoinTableAlias(), sqlBuilder);
                    }
                }

            } else {

                for (int j = 0; j < ff.fields.size(); j++) {
                    String field = ff.fields.get(j);
                    sqlBuilder.append(ff.alias).append(".").append(field).append(COMMA);
                }
            }
        }

        if (sqlBuilder.length() > 0) {
            sqlBuilder.delete(sqlBuilder.length() - COMMA.length(), sqlBuilder.length());
        }

        return sqlBuilder.toString();
    }

    private void addFields(List<ColumnSpec> columnSpecs, String alias, StringBuilder sqlBuilder) {

        for (ColumnSpec columnSpec : columnSpecs) {
            sqlBuilder.append(alias + "." + columnSpec.getColumnName() + COMMA);
        }
    }

    private void addAllFieldsRecursive(List<ColumnSpec> columnSpecs, String alias, StringBuilder sqlBuilder) {

        for (ColumnSpec columnSpec : columnSpecs) {
            sqlBuilder.append(alias + "." + columnSpec.getColumnName() + COMMA);
            JoinSpec joinSpec = columnSpec.getJoinSpec();
            if (joinSpec == null) {
                continue;
            }
            addAllFieldsRecursive(tableSpecMap.get(joinSpec.getJoinTable()).getColumnSpecs(), joinSpec.getJoinTableAlias(), sqlBuilder);
        }
    }

    public static void main(String[] args) {

        List<TableSpec> list = SqlUtils.makeDefaults(TestCases.createList());

        Map<String, TableSpec> specMap = SqlUtils.toTableSpecByTableMap(list);

        SqlQueryGeneratorImpl generator = new SqlQueryGeneratorImpl(specMap, SqlUtils.toColumnSpecMapByName(specMap), SqlUtils.toColumnSpecMapByColumnMap(specMap));

        String users = null;


        users = generator.toSql("contact", ImmutableList.of(
            new SqlFieldBuilder()
                .setPath("contact")
                .setFields(ImmutableList.of("id", "name", "email"))
                .createSqlField(),
            new SqlFieldBuilder()
                .setPath("br_id.house_id.area_id")
                .setFields(
                    ImmutableList.of(
                        "**"
                    )
                )
                .createSqlField(),
            new SqlFieldBuilder()
                .setPath("br_id")
                .setFields(ImmutableList.of("name", "house_name"))
                .createSqlField(),
            new SqlFieldBuilder()
                .setPath("area_id")
                .setFields(ImmutableList.of("name", "regin_name"))
                .createSqlField(),
            new SqlFieldBuilder()
                .setPath("supervisor_id")
                .setFields(ImmutableList.of("name"))
                .createSqlField()
        ));

        System.out.println(users);
    }

    private static class TableAndPath {
        private final String table;
        private final String alias;
        private final List<String> path;

        public TableAndPath(String table, String alias, List<String> path) {
            this.table = table;
            this.alias = alias;
            this.path = path;
        }
    }

    private class TblPathFields {
        private final String table;
        private final String alias;
        private final List<String> path;
        private final List<String> fields;

        private TblPathFields(String table, String alias, List<String> path, List<String> fields) {
            this.table = table;
            this.alias = alias;
            this.path = path;
            this.fields = fields;
        }
    }
}
