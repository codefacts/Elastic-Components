package elasta.orm;

/**
 * Created by Jango on 9/14/2016.
 */
public interface TableJoin {

    String toSqlString();

    StringBuilder appendTo(StringBuilder stringBuilder);
}
