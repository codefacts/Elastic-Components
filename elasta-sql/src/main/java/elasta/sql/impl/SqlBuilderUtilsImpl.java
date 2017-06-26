package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.criteria.Func;
import elasta.criteria.funcs.FieldFunc;
import elasta.criteria.Ops;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.SqlUtils;
import elasta.sql.core.*;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by Jango on 10/12/2016.
 */
final public class SqlBuilderUtilsImpl implements SqlBuilderUtils {

    public List<Func> toWhereFuncs(Collection<SqlCriteria> sqlCriterias) {
        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        sqlCriterias.stream()
            .map(sqlCriteria -> Ops.eq(
                new FieldFunc(sqlCriteria.getAlias() + "." + sqlCriteria.getColumn()),
                Ops.valueOfObj(sqlCriteria.getValue())
            ))
            .forEach(builder::add);

        return builder.build();
    }

    public Collection<JoinData> toJoinDatas(String parentAlias, Collection<SqlJoin> sqlJoins) {

        ImmutableList.Builder<JoinData> builder = ImmutableList.builder();

        for (SqlJoin sqlJoin : sqlJoins) {
            builder.add(
                new JoinData(
                    parentAlias,
                    sqlJoin.getJoinType(),
                    sqlJoin.getJoinTable(),
                    sqlJoin.getAlias(),
                    toColumnToColumnMappings(sqlJoin.getAlias(), sqlJoin.getSqlJoinColumns())
                )
            );
        }

        return builder.build();
    }

    public List<Func> toSelectFuncs(Collection<SqlSelection> sqlSelections) {

        ImmutableList.Builder<Func> funcBuilder = ImmutableList.builder();

        sqlSelections.stream().map(sqlSelection -> new FieldFunc(
            sqlSelection.getAlias() + "." + sqlSelection.getColumn()
        )).forEach(funcBuilder::add);

        return funcBuilder.build();
    }

    public List<Func> toWhereFuncs(String rootAlias, Collection<SqlCondition> sqlConditions) {

        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        for (SqlCondition sqlCondition : sqlConditions) {
            builder.add(
                Ops.eq(new FieldFunc(rootAlias + "." + sqlCondition.getColumn()), Ops.valueOfObj(sqlCondition.getValue()))
            );
        }

        return builder.build();
    }

    public List<Func> toWhereFuncs2(String alias, JsonObject whereCriteria) {
        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        whereCriteria.fieldNames().forEach(column -> builder.add(
            Ops.eq(new FieldFunc(alias + "." + column), Ops.valueOfObj(whereCriteria.getValue(column)))
        ));

        return builder.build();
    }

    public List<Func> toSelectFuncs(String alias, Collection<String> selectedColumns) {

        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        selectedColumns.forEach(column -> builder.add(
            new FieldFunc(alias + "." + column)
        ));

        return builder.build();
    }

    public UpdateTpl toDeleteUpdateTpl(String table, JsonObject where) {
        return new UpdateTpl(
            UpdateOperationType.DELETE,
            table,
            SqlUtils.emptyJsonObject(),
            toSqlCriteria(where)
        );
    }

    public UpdateTpl toInsertUpdateTpl(String table, JsonObject jsonObject) {
        return new UpdateTpl(
            UpdateOperationType.INSERT,
            table,
            jsonObject,
            ImmutableList.of()
        );
    }

    public Collection<UpdateTpl> toInsertUpdateTpls(String table, Collection<JsonObject> jsonObjects) {

        ImmutableList.Builder<UpdateTpl> builder = ImmutableList.builder();

        jsonObjects.stream().map(jsonObject -> toInsertUpdateTpl(table, jsonObject)).forEach(builder::add);

        return builder.build();
    }

    private List<ColumnToColumnMapping> toColumnToColumnMappings(String alias, List<SqlJoinColumn> sqlJoinColumns) {
        ImmutableList.Builder<ColumnToColumnMapping> builder = ImmutableList.builder();

        sqlJoinColumns.stream().map(sqlJoinColumn -> new ColumnToColumnMapping(
            alias + "." + sqlJoinColumn.getJoinTableColumn(),
            sqlJoinColumn.getParentTableAlias() + "." + sqlJoinColumn.getParentTableColumn()
        )).forEach(builder::add);

        return builder.build();
    }

    private Collection<SqlCondition> toSqlCriteria(JsonObject where) {
        ImmutableList.Builder<SqlCondition> builder = ImmutableList.builder();
        for (String column : where.fieldNames()) {
            builder.add(
                new SqlCondition(column, where.getValue(column))
            );
        }
        return builder.build();
    }

    public static void main(String[] asdf) {

    }
}
