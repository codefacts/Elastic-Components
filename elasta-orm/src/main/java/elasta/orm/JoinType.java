package elasta.orm;

/**
 * Created by Jango on 9/15/2016.
 */
public enum JoinType {
    INNER_JOIN("inner join"),
    LEFT_INNER_JOIN("left inner join"),
    RIGHT_INNER_JOIN("right inner join"),
    OUTER_JOIN("outer join"),
    LEFT_OUTER_JOIN("left outer join"),
    RIGHT_OUTER_JOIN("right outer join");

    private final String value;

    JoinType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
