package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.Relationship;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectDbColumnMapping;
import elasta.orm.entity.core.columnmapping.IndirectDbColumnMapping;
import elasta.orm.entity.core.columnmapping.VirtualDbColumnMapping;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.core.JoinTpl;
import elasta.orm.query.expression.impl.JoinData;
import elasta.orm.query.expression.impl.QueryImpl;
import elasta.orm.upsert.ColumnToColumnMapping;
import elasta.sql.core.JoinType;

import java.util.*;
import java.util.stream.Collectors;

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
