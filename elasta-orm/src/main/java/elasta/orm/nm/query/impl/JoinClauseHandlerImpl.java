package elasta.orm.nm.query.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.json.sql.core.JoinType;
import elasta.orm.nm.query.Cqr;
import elasta.orm.nm.query.JoinClauseHandler;
import elasta.orm.nm.query.TableAliasPair;
import elasta.orm.nm.upsert.ColumnToColumnMapping;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class JoinClauseHandlerImpl implements JoinClauseHandler {
    final TableAliasPair rootTableAliasPair;
    final List<JoinData> joinDatas;

    public JoinClauseHandlerImpl(TableAliasPair rootTableAliasPair, List<JoinData> joinDatas) {
        Objects.requireNonNull(rootTableAliasPair);
        Objects.requireNonNull(joinDatas);
        this.rootTableAliasPair = rootTableAliasPair;
        this.joinDatas = joinDatas;
    }

    @Override
    public String toSql() {
        final StringBuilder builder = new StringBuilder();

        for (JoinData joinData : joinDatas) {
            builder
                .append(joinData.getJoinType().getValue()).append(" ")
                .append(joinData.getTable()).append(" ")
                .append(joinData.getAlias()).append(" on ");

            joinData.getColumnToColumnMappings().forEach(columnToColumnMapping -> {
                builder
                    .append(joinData.getParentAlias()).append(".").append(columnToColumnMapping.getSrcColumn()).append(" = ")
                    .append(joinData.getAlias()).append(".").append(columnToColumnMapping.getDstColumn()).append(Cqr._AND_);
            });
            builder.delete(builder.length() - Cqr._AND_.length(), builder.length());
        }

        return rootTableAliasPair.getTable() + " " + rootTableAliasPair.getAlias() + " " + builder.toString();
    }

    public static void main(String[] args) {
        String sql = new JoinClauseHandlerImpl(
            new TableAliasPair("company", "c"),
            ImmutableList.of(
                new JoinData(
                    "c",
                    JoinType.INNER_JOIN,
                    "address",
                    "ad",
                    ImmutableList.of(
                        new ColumnToColumnMapping("addressId", "id"),
                        new ColumnToColumnMapping("pc1", "cc1"),
                        new ColumnToColumnMapping("pc2", "cc2")
                    )
                )
            )
        ).toSql();
        System.out.println(sql);
    }
}
