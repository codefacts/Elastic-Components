package elasta.orm.jpa;

/**
 * Created by Jango on 10/2/2016.
 */
public interface ModelInfoProvider {

    <T> Class<T> get(String model);

    String primaryKey(String model);
}
