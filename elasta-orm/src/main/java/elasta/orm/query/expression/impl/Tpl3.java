package elasta.orm.query.expression.impl;

import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.core.AliasAndColumn;
import lombok.Value;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 4/10/2017.
 */
@Value
final class Tpl3 {
    final Map<FieldExpression, AliasAndColumn> selectFieldExpressionToAliasAndColumnMap;
    final Map<FieldExpression, AliasAndColumn> fieldExpToAliasAndColumnMap;
    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap;

    Tpl3(Map<FieldExpression, AliasAndColumn> selectFieldExpressionToAliasAndColumnMap, Map<FieldExpression, AliasAndColumn> fieldExpToAliasAndColumnMap, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap) {
        Objects.requireNonNull(selectFieldExpressionToAliasAndColumnMap);
        Objects.requireNonNull(fieldExpToAliasAndColumnMap);
        Objects.requireNonNull(aliasToJoinTplMap);
        this.selectFieldExpressionToAliasAndColumnMap = selectFieldExpressionToAliasAndColumnMap;
        this.fieldExpToAliasAndColumnMap = fieldExpToAliasAndColumnMap;
        this.aliasToJoinTplMap = aliasToJoinTplMap;
    }
}
