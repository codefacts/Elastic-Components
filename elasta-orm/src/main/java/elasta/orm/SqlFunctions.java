package elasta.orm;

/**
 * Created by Jango on 9/16/2016.
 */
public enum SqlFunctions {
    UPPER("$upper"), LOWER("$lower"), LENGTH("$length"), ABS("$abs"), CIEL("$ceil"), FLOOR("$floor");

    private final String value;

    SqlFunctions(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
