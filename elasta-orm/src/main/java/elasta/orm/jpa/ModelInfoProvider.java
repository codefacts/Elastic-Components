package elasta.orm.jpa;

/**
 * Created by Jango on 10/2/2016.
 */
public interface ModelInfoProvider {

    ModelInfo get(String model);

    String primaryKey(String model);


}
