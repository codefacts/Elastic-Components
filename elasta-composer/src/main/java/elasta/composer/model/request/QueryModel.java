package elasta.composer.model.request;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by sohan on 5/12/2017.
 */
public interface QueryModel {
    String alias = "alias";
    String joinParams = "joinParams";
    String criteria = "criteria";
    String selections = "selections";
    String orderBy = "orderBy";
    String groupBy = "groupBy";
    String having = "having";
    String page = "page";
    String pageSize = "pageSize";
    String paginationKey = "paginationKey";
}
