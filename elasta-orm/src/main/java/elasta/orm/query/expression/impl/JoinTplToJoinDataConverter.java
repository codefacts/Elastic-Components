package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.Relationship;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectDbColumnMapping;
import elasta.orm.entity.core.columnmapping.IndirectDbColumnMapping;
import elasta.orm.entity.core.columnmapping.VirtualDbColumnMapping;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.core.JoinTpl;
import elasta.sql.core.ColumnToColumnMapping;
import elasta.sql.core.JoinData;
import elasta.sql.core.JoinType;

import java.util.List;
import java.util.Objects;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 4/11/2017.
 */
class JoinTplToJoinDataConverter {
    final EntityMappingHelper helper;
    final AliasGenerator aliasGenerator;

    public JoinTplToJoinDataConverter(EntityMappingHelper helper, AliasGenerator aliasGenerator) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(aliasGenerator);
        this.helper = helper;
        this.aliasGenerator = aliasGenerator;
    }

    List<JoinData> createJoinData(JoinTpl joinTpl) {

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

        String relationTableAlias = aliasGenerator.generate();

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
}
