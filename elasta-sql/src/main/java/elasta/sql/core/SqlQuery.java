package elasta.sql.core;

import com.google.common.collect.ImmutableList;
import elasta.criteria.Func;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 4/11/2017.
 */
@Value
@Builder
public class SqlQuery {
    final List<Func> selectFuncs;
    final TableAliasPair tableAliasPair;
    final Collection<JoinData> joinDatas;
    final List<Func> whereFuncs;
    final List<Func> havingFuncs;
    final List<OrderByData> orderByDatas;
    final List<ColumnAliasPair> groupBy;
    final SqlPagination sqlPagination;

    SqlQuery(List<Func> selectFuncs, TableAliasPair tableAliasPair, Collection<JoinData> joinDatas, List<Func> whereFuncs, List<Func> havingFuncs, List<OrderByData> orderByDatas, List<ColumnAliasPair> groupBy, SqlPagination sqlPagination) {
        Objects.requireNonNull(selectFuncs);
        Objects.requireNonNull(tableAliasPair);
        this.selectFuncs = selectFuncs;
        this.tableAliasPair = tableAliasPair;
        this.joinDatas = joinDatas == null ? ImmutableList.of() : joinDatas;
        this.whereFuncs = whereFuncs == null ? ImmutableList.of() : whereFuncs;
        this.havingFuncs = havingFuncs == null ? ImmutableList.of() : havingFuncs;
        this.orderByDatas = orderByDatas == null ? ImmutableList.of() : orderByDatas;
        this.groupBy = groupBy == null ? ImmutableList.of() : groupBy;
        this.sqlPagination = (sqlPagination == null) ? null : sqlPagination;
    }

    public Optional<SqlPagination> getSqlPagination() {
        return Optional.ofNullable(sqlPagination);
    }

}
