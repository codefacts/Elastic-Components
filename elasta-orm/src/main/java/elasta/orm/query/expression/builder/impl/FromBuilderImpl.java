package elasta.orm.query.expression.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.query.expression.PathExpression;import elasta.sql.core.JoinType;
import elasta.orm.query.expression.builder.FromBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Jango on 17/02/09.
 */
final public class FromBuilderImpl implements FromBuilder {
    String rootEntity;
    String alias;
    final List<PathExpressionAndAliasPair> pairs = new ArrayList<>();

    @Override
    public FromBuilder root(String rootEntity, String alias) {
        this.rootEntity = rootEntity;
        this.alias = alias;
        return this;
    }

    @Override
    public FromBuilder join(PathExpression collectionPath, String collectionAlias, Optional<JoinType> joinType) {
        pairs.add(
            new PathExpressionAndAliasPair(collectionPath, collectionAlias, joinType)
        );
        return this;
    }

    @Override
    public String rootEntity() {
        return rootEntity;
    }

    @Override
    public String rootAlias() {
        return alias;
    }

    @Override
    public List<PathExpressionAndAliasPair> build() {
        return ImmutableList.copyOf(pairs);
    }

    public String getRootEntity() {
        return rootEntity;
    }

    public void setRootEntity(String rootEntity) {
        this.rootEntity = rootEntity;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
