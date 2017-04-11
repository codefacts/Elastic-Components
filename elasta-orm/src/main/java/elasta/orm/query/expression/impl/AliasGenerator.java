package elasta.orm.query.expression.impl;

import java.util.Objects;

/**
 * Created by sohan on 4/11/2017.
 */
class AliasGenerator {
    private final String aliasStr;
    private int count = 0;

    public AliasGenerator(String aliasStr) {
        Objects.requireNonNull(aliasStr);
        this.aliasStr = aliasStr;
    }

    String generate() {
        return aliasStr + String.valueOf(count++);
    }
}
