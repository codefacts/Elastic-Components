package elasta.orm.nm.query;

/**
 * Created by Jango on 17/02/09.
 */
public interface PathExpression {

    PathExpression getParent();

    String[] parts();

    String getAt(int index);

    int size();

    String last();

    static PathExpression create(String... parts) {
        return null;
    }
}
