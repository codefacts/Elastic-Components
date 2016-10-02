package elasta.orm.json.sql.criteria;

/**
 * Created by Jango on 9/16/2016.
 */
public interface SqlSymbolTranslator {
    SqlSymbolSpec translate(String key, Object value);
}
