package elasta.orm.query.expression.core;

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
    final ImmutableMap<String, JoinTpl> aliasToJoinTplMap;
    final String ALIAS_STR;
    final QueryImpl.AliasCounter aliasCounter;
    final EntityMappingHelper helper;

    public JoinDataBuilder(String rootAlias, ImmutableMap<String, JoinTpl> aliasToJoinTplMap, String ALIAS_STR, QueryImpl.AliasCounter aliasCounter, EntityMappingHelper helper) {
        this.ALIAS_STR = ALIAS_STR;
        this.aliasCounter = aliasCounter;
        this.helper = helper;
        Objects.requireNonNull(rootAlias);
        Objects.requireNonNull(aliasToJoinTplMap);
        this.rootAlias = rootAlias;
        this.aliasToJoinTplMap = aliasToJoinTplMap;
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
        context.put(joinTpl, createJoinData(joinTpl));
    }

    private List<JoinData> createJoinData(JoinTpl joinTpl) {

        Field field = helper.getField(joinTpl.getParentEntity(), joinTpl.getChildEntityField());

        Relationship relationship = field.getRelationship().orElseThrow(() -> new QueryParserException("No child '" + joinTpl.getChildEntity() + "' found in " + joinTpl.getParentEntity() + "." + joinTpl.getChildEntityField()));

        if (not(relationship.getEntity().equals(joinTpl.getChildEntity()))) {
            throw new QueryParserException("No child '" + joinTpl.getChildEntity() + "' found in " + joinTpl.getParentEntity() + "." + joinTpl.getChildEntityField());
        }

        DbColumnMapping columnMapping = helper.getColumnMapping(joinTpl.getParentEntity(), joinTpl.getChildEntityField());

        switch (columnMapping.getColumnType()) {

            case DIRECT:
                return directColumnMapping((DirectDbColumnMapping) columnMapping, joinTpl);

            case INDIRECT:
                return indirectColumnMapping((IndirectDbColumnMapping) columnMapping, joinTpl);

            case VIRTUAL:
                return virtualColumnMapping((VirtualDbColumnMapping) columnMapping, joinTpl);
        }

        throw new QueryParserException("Invalid or no relationship between parent '" + joinTpl.getParentEntity() + "' and child '" + joinTpl.getChildEntity() + "'");
    }

    private List<JoinData> virtualColumnMapping(VirtualDbColumnMapping columnMapping, JoinTpl joinTpl) {

        ImmutableList.Builder<ColumnToColumnMapping> listBuilder = ImmutableList.builder();

        columnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
            listBuilder.add(
                new ColumnToColumnMapping(
                    foreignColumnMapping.getDstColumn(),
                    foreignColumnMapping.getSrcColumn()
                )
            );
        });

        return ImmutableList.of(
            new JoinData(
                joinTpl.getParentEntityAlias(),
                joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                helper.getTable(joinTpl.getChildEntity()),
                joinTpl.getChildEntityAlias(),
                listBuilder.build()
            )
        );
    }

    private List<JoinData> indirectColumnMapping(IndirectDbColumnMapping columnMapping, JoinTpl joinTpl) {

        ImmutableList.Builder<JoinData> joinDataListBuilder = ImmutableList.builder();

        ImmutableList.Builder<ColumnToColumnMapping> mappingBuilder1 = ImmutableList.builder();

        columnMapping.getSrcForeignColumnMappingList().forEach(foreignColumnMapping -> {
            mappingBuilder1.add(
                new ColumnToColumnMapping(
                    foreignColumnMapping.getSrcColumn(),
                    foreignColumnMapping.getDstColumn()
                )
            );
        });

        String relationTableAlias = createAlias();

        joinDataListBuilder.add(
            new JoinData(
                joinTpl.getParentEntityAlias(),
                joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                columnMapping.getRelationTable(),
                relationTableAlias,
                mappingBuilder1.build()
            )
        );

        ImmutableList.Builder<ColumnToColumnMapping> mappingBuilder2 = ImmutableList.builder();

        columnMapping.getDstForeignColumnMappingList().forEach(foreignColumnMapping -> {
            mappingBuilder2.add(
                new ColumnToColumnMapping(
                    foreignColumnMapping.getDstColumn(),
                    foreignColumnMapping.getSrcColumn()
                )
            );
        });

        joinDataListBuilder.add(
            new JoinData(
                relationTableAlias,
                joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                helper.getDbMapping(joinTpl.getChildEntity()).getTable(),
                joinTpl.getChildEntityAlias(),
                mappingBuilder2.build()
            )
        );

        return joinDataListBuilder.build();
    }

    private List<JoinData> directColumnMapping(DirectDbColumnMapping directDbColumnMapping, JoinTpl joinTpl) {
        ImmutableList.Builder<ColumnToColumnMapping> mappingListBuilder = ImmutableList.builder();

        directDbColumnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
            mappingListBuilder.add(
                new ColumnToColumnMapping(
                    foreignColumnMapping.getSrcColumn(),
                    foreignColumnMapping.getDstColumn()
                )
            );
        });

        return ImmutableList.of(
            new JoinData(
                joinTpl.getParentEntityAlias(),
                joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                helper.getEntity(joinTpl.getChildEntity()).getDbMapping().getTable(),
                joinTpl.getChildEntityAlias(),
                mappingListBuilder.build()
            )
        );
    }

    private String createAlias() {
        return ALIAS_STR + String.valueOf(aliasCounter.aliasCount++);
    }
}
