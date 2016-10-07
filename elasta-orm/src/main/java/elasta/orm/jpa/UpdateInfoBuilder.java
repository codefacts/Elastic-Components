package elasta.orm.jpa;

import io.vertx.core.json.JsonObject;

public class UpdateInfoBuilder {
    private boolean insert;
    private String model;
    private JsonObject data;

    public UpdateInfoBuilder setInsert(boolean insert) {
        this.insert = insert;
        return this;
    }

    public UpdateInfoBuilder setModel(String model) {
        this.model = model;
        return this;
    }

    public UpdateInfoBuilder setData(JsonObject data) {
        this.data = data;
        return this;
    }

    public UpdateInfo createUpdateInfo() {
        return new UpdateInfo(insert, model, data);
    }
}