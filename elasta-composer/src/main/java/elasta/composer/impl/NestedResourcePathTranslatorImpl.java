package elasta.composer.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.composer.NestedResourcePathTranslator;
import elasta.composer.ParameterFieldsInfoProvider;
import elasta.composer.ex.NestedResourcePathTranslatorException;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.Relationship;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.PathExpression;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 5/25/2017.
 */
final public class NestedResourcePathTranslatorImpl implements NestedResourcePathTranslator {
    final String splitBy;
    final EntityMappingHelper helper;
    final ParameterFieldsInfoProvider parameterFieldsInfoProvider;

    public NestedResourcePathTranslatorImpl(String splitBy, EntityMappingHelper helper, ParameterFieldsInfoProvider parameterFieldsInfoProvider) {
        Objects.requireNonNull(splitBy);
        Objects.requireNonNull(helper);
        Objects.requireNonNull(parameterFieldsInfoProvider);
        this.splitBy = splitBy;
        this.helper = helper;
        this.parameterFieldsInfoProvider = parameterFieldsInfoProvider;
    }

    @Override
    public QueryParamsAndFullPath translate(String rootEntity, String nestedResourcePath) {
        Objects.requireNonNull(rootEntity);
        Objects.requireNonNull(nestedResourcePath);

        readAll(rootEntity, "r", Arrays.asList(
            nestedResourcePath.trim().split(splitBy)
        ));

        return null;
    }

    private void readAll(String rootEntity, final String rootAlias, List<String> parts) {

        readRecursive(
            new BuilderContext(
                parts
            ),
            0,
            rootEntity,
            PathExpression.create(rootAlias)
        );

    }

    private void readRecursive(BuilderContext builderContext, int startIndex, String entity, PathExpression pathExpression) {
        final List<String> parts = builderContext.parts;

        final Set<String> referencingFields = Arrays.stream(helper.getFields(entity)).filter(field -> field.getRelationship().isPresent()).map(Field::getName).collect(Collectors.toSet());

        int index = startIndex;

        final FieldValueParser parser = new FieldValueParser(pathExpression, parameterFieldsInfoProvider.get(entity));

        for (; index < parts.size(); index++) {
            final String part = parts.get(index);

            if (referencingFields.contains(part)) {
                break;
            }

            if (parser.isComplete()) {

                if (index < parts.size() - 1 && Utils.not(referencingFields.contains(parts.get(index + 1)))) {
                    throw new NestedResourcePathTranslatorException("Malformed resource path");
                }

                break;
            }

            parser.add(part);
        }

        if (index == startIndex) {
            throw new NestedResourcePathTranslatorException("Malformed path '" + builderContext.parts + "'");
        }

        if (index >= parts.size() - 1) {
            return;
        }

        final String childField = parts.get(index);
        final Relationship relationship = helper.getField(entity, childField).getRelationship().get();

        final String joinAlias = "d" + String.valueOf(index);

        if (relationship.getName() == Relationship.Name.HAS_MANY) {

            builderContext.joins.add(
                QueryExecutor.JoinParam.builder()
                    .path(pathExpression.concat(childField))
                    .alias(joinAlias)
                    .build()
            );

            readRecursive(
                builderContext,
                startIndex,
                relationship.getEntity(),
                PathExpression.create(joinAlias)
            );
            return;
        }

        readRecursive(
            builderContext,
            startIndex,
            relationship.getEntity(),
            pathExpression.concat(childField)
        );
    }

    public static void main(String[] asdf) {
        System.out.println(Arrays.toString("c/a/b/d/e/f/g/h".split("/")));
    }

    static final class BuilderContext {
        final List<String> parts;
        final ImmutableList.Builder<PathExpression> selections = ImmutableList.builder();
        final ImmutableList.Builder<QueryExecutor.JoinParam> joins = ImmutableList.builder();
        final ImmutableList.Builder<PathAndValue> criterias = ImmutableList.builder();

        BuilderContext(List<String> parts) {
            Objects.requireNonNull(parts);
            this.parts = parts;
        }
    }

    @Value
    static final class PathAndValue {
        final PathExpression pathExpression;
        final Object value;

        PathAndValue(PathExpression pathExpression, Object value) {
            Objects.requireNonNull(pathExpression);
            Objects.requireNonNull(value);
            this.pathExpression = pathExpression;
            this.value = value;
        }
    }

    static final class FieldValueParser {
        final PathExpression pathExpression;
        final List<ParameterFieldsInfoProvider.FieldInfo> fieldInfos;
        final ImmutableList.Builder<PathAndValue> pathAndValueBuilder = ImmutableList.builder();
        int size = 0;
        Optional<String> field = Optional.empty();

        public FieldValueParser(PathExpression pathExpression, List<ParameterFieldsInfoProvider.FieldInfo> fieldInfos) {
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
