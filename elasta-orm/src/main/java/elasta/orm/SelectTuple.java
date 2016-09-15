package elasta.orm;

/**
 * Created by Jango on 9/15/2016.
 */
final public class SelectTuple {
    private final String alias;
    private final String field;

    public SelectTuple(String alias, String field) {
        this.alias = alias;
        this.field = field;
    }

    public String getAlias() {
        return alias;
    }

    public String getField() {
        return field;
    }
}
