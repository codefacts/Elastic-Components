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

    public JoinTplMapBuilder(Map<String, Map<String, QueryImpl.PartAndJoinTpl>> joinTplsMap) {
        Objects.requireNonNull(joinTplsMap);
        this.joinTplsMap = joinTplsMap;
    }

    public ImmutableMap<String, JoinTpl> build() {

        final ImmutableMap.Builder<String, JoinTpl> joinTplMapBuilder = ImmutableMap.builder();

        joinTplsMap.forEach((alias, partAndJoinTplMap) -> {

            traverseRecursive(partAndJoinTplMap, joinTplMapBuilder);
        });

        return joinTplMapBuilder.build();
    }

    private void traverseRecursive(Map<String, QueryImpl.PartAndJoinTpl> partAndJoinTplMap, final ImmutableMap.Builder<String, JoinTpl> joinTplMapBuilder) {
        partAndJoinTplMap.forEach((field, partAndJoinTpl) -> {
            traverseRecursive(partAndJoinTpl.partAndJoinTplMap, joinTplMapBuilder);
            joinTplMapBuilder.put(partAndJoinTpl.joinTpl.getChildEntityAlias(), partAndJoinTpl.joinTpl);
        });
    }
}
