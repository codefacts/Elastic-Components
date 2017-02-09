package elasta.orm.nm.query.impl;

import elasta.orm.nm.query.JoinClauseHandler;
import elasta.orm.nm.query.TableAliasPair;

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

        String parentAlias = rootTableAliasPair.getAlias();
        for (JoinData joinData : joinDatas) {
            builder
                .append(joinData.getJoinType().getValue()).append(" ")
                .append(joinData.getTable()).append(" ")
                .append(joinData.getAlias()).append(" on ")
                .append(parentAlias).append(".").append(joinData.parentColumn).append(" = ")
                .append(joinData.getAlias()).append(".").append(joinData.getColumn());
        }

        return rootTableAliasPair.getTable() + " " + rootTableAliasPair.getAlias() + " " + builder.toString();
    }
}
