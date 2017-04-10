package elasta.orm.query.expression.impl;

import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.core.AliasAndColumn;
import lombok.Value;

import java.util.Map;

/**
 * Created by sohan on 4/10/2017.
 */
@Value
public class Tpl2 {
    final Map<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMap;
    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> partAndJoinTplMap;

    public Tpl2(Map<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMap, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> partAndJoinTplMap) {
        this.fieldExpToAliasedColumnMap = fieldExpToAliasedColumnMap;
        this.partAndJoinTplMap = partAndJoinTplMap;
    }
}
