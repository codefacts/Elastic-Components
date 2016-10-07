package elasta.orm.jpa;

import io.vertx.core.json.JsonObject;

/**
 * Created by Shahadat on 10/6/2016.
 */
public class UpdateInfo {
    private final boolean insert;
    private final String model;
    private final JsonObject data;

    public UpdateInfo(boolean insert, String model, JsonObject data) {
        this.insert = insert;
        this.model = model;
        this.data = data;
    }

    public boolean isInsert() {
        return insert;
    }

    public String getModel() {
        return model;
    }

    public JsonObject getData() {
        return data;
    }
}
