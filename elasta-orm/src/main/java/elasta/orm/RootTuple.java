package elasta.orm;

/**
 * Created by Jango on 9/15/2016.
 */
final public class RootTuple {
    private final String root;
    private final String alias;

    public RootTuple(String root, String alias) {
        this.root = root;
        this.alias = alias;
    }

    public String getRoot() {
        return root;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return root + " as " + alias;
    }
}
