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
    final Map<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMap;
    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap;

    Tpl3(Map<FieldExpression, AliasAndColumn> selectFieldExpressionToAliasAndColumnMap, Map<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMap, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap) {
        Objects.requireNonNull(selectFieldExpressionToAliasAndColumnMap);
        Objects.requireNonNull(fieldExpToAliasedColumnMap);
        Objects.requireNonNull(aliasToJoinTplMap);
        this.selectFieldExpressionToAliasAndColumnMap = selectFieldExpressionToAliasAndColumnMap;
        this.fieldExpToAliasedColumnMap = fieldExpToAliasedColumnMap;
        this.aliasToJoinTplMap = aliasToJoinTplMap;
    }
}
