package elasta.orm.sql.criteria;

/**
 * Created by Jango on 9/16/2016.
 */
public class OperatorValuePair {
    private final String op;
    private final Object value;

    public OperatorValuePair(String op, Object value) {
        this.op = op;
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public Object getValue() {
        return value;
    }
}
