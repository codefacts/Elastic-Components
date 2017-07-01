package tracker;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 6/25/2017.
 */
public interface AppUtils {

    String isNewKey = "$isNew";

    int failureCode = 500;
    String anonymous = "anonymous";
}
