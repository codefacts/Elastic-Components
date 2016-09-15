package elasta.orm;

/**
 * Created by Jango on 9/16/2016.
 */
public class SqlSymbolSpec {
    private final String key;
    private final String operator;
    private final Object value;

    public SqlSymbolSpec(String key, String operator, Object value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }
}
