package elasta.sql.core;

import elasta.criteria.Func;
import elasta.sql.core.ColumnAliasPair;
import elasta.sql.core.OrderByData;
import elasta.sql.core.TableAliasPair;
import elasta.sql.core.JoinData;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    final List<ColumnAliasPair> columnAliasPairs;

    public SqlQuery(List<Func> selectFuncs, TableAliasPair tableAliasPair, Collection<JoinData> joinDatas, List<Func> whereFuncs, List<Func> havingFuncs, List<OrderByData> orderByDatas, List<ColumnAliasPair> columnAliasPairs) {
        Objects.requireNonNull(selectFuncs);
        Objects.requireNonNull(tableAliasPair);
        Objects.requireNonNull(joinDatas);
        Objects.requireNonNull(whereFuncs);
        Objects.requireNonNull(havingFuncs);
        Objects.requireNonNull(orderByDatas);
        Objects.requireNonNull(columnAliasPairs);
        this.selectFuncs = selectFuncs;
        this.tableAliasPair = tableAliasPair;
        this.joinDatas = joinDatas;
        this.whereFuncs = whereFuncs;
        this.havingFuncs = havingFuncs;
        this.orderByDatas = orderByDatas;
        this.columnAliasPairs = columnAliasPairs;
    }
}
