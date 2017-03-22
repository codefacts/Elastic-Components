package elasta.orm.query.expression.core;

import elasta.sql.core.JoinType;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by Jango on 17/02/11.
 */
final public class JoinTpl {
    final String parentEntityAlias;
    final String childEntityAlias;
    final String parentEntity;
    final String childEntityField;
    final String childEntity;
    final Optional<JoinType> joinType;

    public JoinTpl(String parentEntityAlias, String childEntityAlias, String parentEntity, String childEntityField, String childEntity, Optional<JoinType> joinType) {
        Objects.requireNonNull(parentEntityAlias);
        Objects.requireNonNull(childEntityAlias);
        Objects.requireNonNull(parentEntity);
        Objects.requireNonNull(childEntityField);
        Objects.requireNonNull(childEntity);
        this.parentEntityAlias = parentEntityAlias;
        this.childEntityAlias = childEntityAlias;
        this.parentEntity = parentEntity;
        this.childEntityField = childEntityField;
        this.childEntity = childEntity;
        this.joinType = joinType != null ? joinType : Optional.empty();
    }

    public String getChildEntityField() {
        return childEntityField;
    }

    public Optional<JoinType> getJoinType() {
        return joinType;
    }

    public String getParentEntityAlias() {
        return parentEntityAlias;
    }

    public String getChildEntityAlias() {
        return childEntityAlias;
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public String getChildEntity() {
        return childEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoinTpl joinTpl = (JoinTpl) o;

        if (childEntityAlias != null ? !childEntityAlias.equals(joinTpl.childEntityAlias) : joinTpl.childEntityAlias != null)
            return false;
        if (parentEntity != null ? !parentEntity.equals(joinTpl.parentEntity) : joinTpl.parentEntity != null)
            return false;
        return childEntity != null ? childEntity.equals(joinTpl.childEntity) : joinTpl.childEntity == null;

    }

    @Override
    public int hashCode() {
        int result = childEntityAlias != null ? childEntityAlias.hashCode() : 0;
        result = 31 * result + (parentEntity != null ? parentEntity.hashCode() : 0);
        result = 31 * result + (childEntity != null ? childEntity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JoinTpl{" +
            "als='" + childEntityAlias + '\'' +
            ", entity='" + parentEntity + '\'' +
            ", childEntity='" + childEntity + '\'' +
            '}';
    }
}
