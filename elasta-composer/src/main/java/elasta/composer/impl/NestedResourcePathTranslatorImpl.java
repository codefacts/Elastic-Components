package elasta.composer.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.composer.NestedResourcePathTranslator;
import elasta.composer.ParameterFieldsInfoProvider;
import elasta.composer.ex.NestedResourcePathTranslatorException;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.Relationship;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 5/25/2017.
 */
final public class NestedResourcePathTranslatorImpl implements NestedResourcePathTranslator {
    final String splitBy;
    final EntityMappingHelper helper;
    final ParameterFieldsInfoProvider parameterFieldsInfoProvider;

    public NestedResourcePathTranslatorImpl(EntityMappingHelper helper, ParameterFieldsInfoProvider parameterFieldsInfoProvider) {
        this("/", helper, parameterFieldsInfoProvider);
    }

    public NestedResourcePathTranslatorImpl(String splitBy, EntityMappingHelper helper, ParameterFieldsInfoProvider parameterFieldsInfoProvider) {
        Objects.requireNonNull(splitBy);
        Objects.requireNonNull(helper);
        Objects.requireNonNull(parameterFieldsInfoProvider);
        this.splitBy = splitBy;
        this.helper = helper;
        this.parameterFieldsInfoProvider = parameterFieldsInfoProvider;
    }

    @Override
    public QueryParamsAndFullPath translate(String rootEntity, String rootAlias, String nestedResourcePath) {
        Objects.requireNonNull(rootEntity);
        Objects.requireNonNull(nestedResourcePath);

        final BuilderContext builderContext = readAll(rootEntity, rootAlias, Arrays.asList(
            nestedResourcePath.trim().split(splitBy)
        ));

        return QueryParamsAndFullPath.builder()
            .selections(builderContext.selections.build())
            .fullPath(PathExpression.create(builderContext.fullPathParts.build()))
            .joins(builderContext.joins.build())
            .criterias(builderContext.criterias.build())
            .build();
    }

    private BuilderContext readAll(final String rootEntity, final String rootAlias, final List<String> parts) {

        final BuilderContext builderContext = new BuilderContext(
            parts,
            rootAlias,
            new FieldExpressionImpl(
                PathExpression.create(rootAlias).concat(
                    helper.getPrimaryKey(rootEntity)
                )
            )
        );

        readRecursive(
            builderContext,
            1,
            rootEntity,
            PathExpression.create(rootAlias)
        );

        return builderContext;
    }

    private void readRecursive(BuilderContext builderContext, int startIndex, String entity, PathExpression pathExpression) {

        final List<String> parts = builderContext.parts;

        final Set<String> referencingFields = Arrays.stream(helper.getFields(entity)).filter(field -> field.getRelationship().isPresent()).map(Field::getName).collect(Collectors.toSet());

        int index = startIndex;

        final List<ParameterFieldsInfoProvider.FieldInfo> fieldInfos = parameterFieldsInfoProvider.get(entity);

        final FieldValueParser parser = new FieldValueParser(pathExpression, fieldInfos);

        for (; index < parts.size(); index++) {

            final String part = parts.get(index);

            if (referencingFields.contains(part)) {
                break;
            }

            if (parser.isComplete()) {

                throw new NestedResourcePathTranslatorException("Malformed resource path");
            }

            parser.add(part);
        }

        {
            List<PathAndValue> pathAndValues = parser.pathAndValueBuilder.build();

            if (pathAndValues.size() > 0 && not(pathAndValues.size() != fieldInfos.size())) {

                throw new NestedResourcePathTranslatorException("Malformed resource path");
            }

            builderContext.criterias.addAll(pathAndValues);
        }

        if (index >= parts.size() - 1) {
            return;
        }

        final String childField = parts.get(index);

        final Relationship relationship = helper.getField(entity, childField).getRelationship().get();
        String childEntity = relationship.getEntity();

        final String joinAlias = "d" + String.valueOf(index);

        final PathExpression fullPathExp = pathExpression.concat(childField);
        final int newIndex = index + 1;


        {
            builderContext.fullPathParts.add(childField);

            builderContext.selections.add(
                new FieldExpressionImpl(
                    pathExpression.concat(helper.getPrimaryKey(childEntity))
                )
            );
        }

        if (relationship.getName() == Relationship.Name.HAS_MANY) {

            builderContext.joins.add(
                QueryExecutor.JoinParam.builder()
                    .path(fullPathExp)
                    .alias(joinAlias)
                    .build()
            );

            readRecursive(
                builderContext,
                newIndex,
                childEntity,
                PathExpression.create(joinAlias)
            );
            return;
        }

        readRecursive(
            builderContext,
            newIndex,
            childEntity,
            fullPathExp
        );
    }

    public static void main(String[] asdf) {
//        System.out.println(Arrays.toString("c/a/b/d/e/f/g/h".split("/")));
        int i = 0;
        for (; i < 10; i++) {
            if (i == 5) {
                break;
            }
        }
        System.out.println(i);
    }

    static final class BuilderContext {
        final List<String> parts;
        final ImmutableList.Builder<FieldExpression> selections = ImmutableList.builder();
        final ImmutableList.Builder<String> fullPathParts = ImmutableList.builder();
        final ImmutableList.Builder<QueryExecutor.JoinParam> joins = ImmutableList.builder();
        final ImmutableList.Builder<PathAndValue> criterias = ImmutableList.builder();

        BuilderContext(List<String> parts, String rootAlias, FieldExpression rootEntityPrimaryKeyPathExp) {
            Objects.requireNonNull(parts);
            Objects.requireNonNull(rootAlias);
            this.parts = parts;
            fullPathParts.add(rootAlias);
            selections.add(rootEntityPrimaryKeyPathExp);
        }
    }

    static final class FieldValueParser {
        final PathExpression pathExpression;
        final List<ParameterFieldsInfoProvider.FieldInfo> fieldInfos;
        final ImmutableList.Builder<PathAndValue> pathAndValueBuilder = ImmutableList.builder();
        int size = 0;
        Optional<String> field = Optional.empty();

        FieldValueParser(PathExpression pathExpression, List<ParameterFieldsInfoProvider.FieldInfo> fieldInfos) {
            Objects.requireNonNull(pathExpression);
            Objects.requireNonNull(fieldInfos);
            this.pathExpression = pathExpression;
            this.fieldInfos = fieldInfos;
        }

        boolean isComplete() {
            return size == fieldInfos.size();
        }

        void add(String part) {

            if (field.isPresent()) {
                addValue(field.get(), part);
                return;
            }

            if (Objects.equals(fieldInfos.get(size).getField(), part)) {
                field = Optional.of(part);
                return;
            }

            if (size >= fieldInfos.size()) {
                throw new NestedResourcePathTranslatorException("Malformed resource path");
            }

            addValue(fieldInfos.get(size).getField(), part);
        }

        private void addValue(String field, String part) {
            pathAndValueBuilder.add(
                new PathAndValue(
                    pathExpression.concat(field), part
                )
            );
            size = size + 1;
            this.field = Optional.empty();
        }
    }
}
