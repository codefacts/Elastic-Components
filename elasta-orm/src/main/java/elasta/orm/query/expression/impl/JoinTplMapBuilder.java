package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.query.expression.core.JoinTpl;

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

    public ImmutableMap<String, JoinData> build() {

        final ImmutableMap.Builder<String, JoinData> joinTplMapBuilder = ImmutableMap.builder();

        joinTplsMap.forEach((alias, partAndJoinTplMap) -> traverseRecursive(partAndJoinTplMap, joinTplMapBuilder));

        return joinTplMapBuilder.build();
    }

    private void traverseRecursive(Map<String, QueryImpl.PartAndJoinTpl> partAndJoinTplMap, final ImmutableMap.Builder<String, JoinData> joinTplMapBuilder) {

        partAndJoinTplMap.forEach((field, partAndJoinTpl) -> {

            traverseRecursive(partAndJoinTpl.partToJoinTplMap, joinTplMapBuilder);

            joinTplToJoinDataConverter.createJoinData(partAndJoinTpl.joinTpl).forEach(joinData -> {

                joinTplMapBuilder.put(joinData.getAlias(), joinData);
            });
        });
    }
}
