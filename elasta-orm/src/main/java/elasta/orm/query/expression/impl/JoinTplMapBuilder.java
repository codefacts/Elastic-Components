package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.sql.core.JoinData;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 4/10/2017.
 */
final public class JoinTplMapBuilder {
    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> joinTplsMap;
    final JoinTplToJoinDataConverter joinTplToJoinDataConverter;

    JoinTplMapBuilder(Map<String, Map<String, QueryImpl.PartAndJoinTpl>> joinTplsMap, JoinTplToJoinDataConverter joinTplToJoinDataConverter) {
        Objects.requireNonNull(joinTplsMap);
        Objects.requireNonNull(joinTplToJoinDataConverter);
        this.joinTplsMap = joinTplsMap;
        this.joinTplToJoinDataConverter = joinTplToJoinDataConverter;
    }

    public ImmutableList<JoinData> build() {

        final ImmutableList.Builder<JoinData> joinDataListBuilder = ImmutableList.builder();

        joinTplsMap.forEach((alias, partAndJoinTplMap) -> traverseRecursive(partAndJoinTplMap, joinDataListBuilder));

        return joinDataListBuilder.build();
    }

    private void traverseRecursive(Map<String, QueryImpl.PartAndJoinTpl> partAndJoinTplMap, final ImmutableList.Builder<JoinData> joinDataListBuilder) {

        partAndJoinTplMap.forEach((field, partAndJoinTpl) -> {

            traverseRecursive(partAndJoinTpl.partToJoinTplMap, joinDataListBuilder);

            joinTplToJoinDataConverter.createJoinData(partAndJoinTpl.joinTpl).forEach(joinDataListBuilder::add);
        });
    }
}
