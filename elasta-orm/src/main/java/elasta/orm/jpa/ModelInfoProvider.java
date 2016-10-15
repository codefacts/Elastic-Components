package elasta.orm.jpa;

import elasta.orm.jpa.models.ModelInfo;

import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
public interface ModelInfoProvider {

    ModelInfo get(String model);

    ModelInfo get(String model, List<String> path);

    String primaryKey(String model);

    String primaryKey(String model, List<String> path);

}
