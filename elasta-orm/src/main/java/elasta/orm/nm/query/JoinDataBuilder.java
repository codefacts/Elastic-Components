package elasta.orm.nm.query;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.nm.query.impl.JoinData;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 17/02/19.
 */
final public class JoinDataBuilder {
    final String rootAlias;
    final ImmutableMap<String, JoinTpl> aliasToJoinTplMap;
    final Function<JoinTpl, List<JoinData>> createJoinData;

    public JoinDataBuilder(String rootAlias, ImmutableMap<String, JoinTpl> aliasToJoinTplMap, Function<JoinTpl, List<JoinData>> createJoinData) {
        Objects.requireNonNull(rootAlias);
        Objects.requireNonNull(aliasToJoinTplMap);
        Objects.requireNonNull(createJoinData);
        this.rootAlias = rootAlias;
        this.aliasToJoinTplMap = aliasToJoinTplMap;
        this.createJoinData = createJoinData;
    }

    public List<JoinData> build() {

        final LinkedHashMap<JoinTpl, List<JoinData>> context = new LinkedHashMap<>();

        aliasToJoinTplMap.forEach((alias, joinTpl) -> {
            addTo(joinTpl, context);
        });

        return context.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private void addTo(JoinTpl joinTpl, LinkedHashMap<JoinTpl, List<JoinData>> context) {
        if (context.containsKey(joinTpl)) {
            return;
        }
        if (not(joinTpl.getParentEntityAlias().equals(rootAlias))) {
            addTo(aliasToJoinTplMap.get(joinTpl.getParentEntityAlias()), context);
        }
        context.put(joinTpl, createJoinData.apply(joinTpl));
    }
}
