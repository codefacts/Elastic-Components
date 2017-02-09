package elasta.orm.nm.query;

/**
 * Created by Jango on 17/02/09.
 */
public enum Order {
    ASC("asc"), DESC("desc");
    private final String val;

    Order(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "Order{" +
            "val='" + val + '\'' +
            '}';
    }
}
