package elasta.orm;

import elasta.core.promise.intfs.Promise;
import elasta.orm.query.QueryExecutor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/19/2017.
 */
public interface BaseOrm {

    Promise<JsonObject> upsert(UpsertParams params);

    Promise<JsonObject> delete(DeleteParams params);

    Promise<JsonObject> deleteChildRelations(
        DeleteChildRelationsParams params
    );

    Promise<List<JsonObject>> query(
        QueryExecutor.QueryParams params
    );

    Promise<List<JsonArray>> queryArray(
        QueryExecutor.QueryArrayParams params
    );

    @Value
    @Builder
    final class UpsertParams {
        final String entity;
        final JsonObject jsonObject;

        UpsertParams(String entity, JsonObject jsonObject) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(jsonObject);
            this.entity = entity;
            this.jsonObject = jsonObject;
        }
    }

    @Value
    @Builder
    final class DeleteParams {
        final String entity;
        final JsonObject jsonObject;

        DeleteParams(String entity, JsonObject jsonObject) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(jsonObject);
            this.entity = entity;
            this.jsonObject = jsonObject;
        }
    }

    @Value
    @Builder
    final class DeleteChildRelationsParams {
        final String entity;
        final JsonObject jsonObject;

        DeleteChildRelationsParams(String entity, JsonObject jsonObject) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(jsonObject);
            this.entity = entity;
            this.jsonObject = jsonObject;
        }
    }
}
