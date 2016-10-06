package elasta.orm.jpa;

import elasta.orm.jpa.models.ModelInfo;

/**
 * Created by Jango on 10/2/2016.
 */
public interface ModelInfoProvider {

    ModelInfo get(String model);

    String primaryKey(String model);

}
