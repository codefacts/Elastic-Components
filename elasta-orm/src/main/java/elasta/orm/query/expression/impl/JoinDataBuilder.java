package elasta.orm.query.expression.impl;

import elasta.sql.core.JoinData;

import java.util.*;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 17/02/19.
 */
final public class JoinDataBuilder {
    final String rootAlias;
    final Map<String, JoinData> aliasToJoinTplMap;

    public JoinDataBuilder(String rootAlias, Map<String, JoinData> aliasToJoinTplMap) {
        this.rootAlias = rootAlias;
        this.aliasToJoinTplMap = aliasToJoinTplMap;
    }

    public Set<JoinData> build() {

        final Set<JoinData> joinDataSet = new LinkedHashSet<>();

        aliasToJoinTplMap.forEach((alias, joinTpl) -> {
            addTo(joinTpl, joinDataSet);
        });

        return joinDataSet;
    }

    private void addTo(JoinData joinData, Set<JoinData> context) {
        if (context.contains(joinData)) {
            return;
        }
        if (not(joinData.getParentAlias().equals(rootAlias))) {
            addTo(aliasToJoinTplMap.get(joinData.getParentAlias()), context);
        }
        context.add(joinData);
    }
}
