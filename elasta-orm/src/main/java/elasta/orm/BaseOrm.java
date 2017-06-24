package elasta.orm;

import elasta.core.promise.intfs.Promise;
import elasta.orm.query.QueryExecutor;
import elasta.orm.upsert.TableData;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/19/2017.
 */
public interface BaseOrm {

    Promise<List<UpdateTpl>> upsert(UpsertParams params);

    Promise<List<UpdateTpl>> delete(DeleteParams params);

    Promise<List<UpdateTpl>> deleteChildRelations(
        DeleteChildRelationsParams params
    );

    Promise<List<UpdateTpl>> execute(Collection<ExecuteParams> paramss);

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

        public UpsertParams(String entity, JsonObject jsonObject) {
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

        public DeleteParams(String entity, JsonObject jsonObject) {
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

        public DeleteChildRelationsParams(String entity, JsonObject jsonObject) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(jsonObject);
            this.entity = entity;
            this.jsonObject = jsonObject;
        }
    }

    @Value
    @Builder
    final class ExecuteParams {
        final OperationType operationType;
        final String entity;
        final JsonObject jsonObject;

        public ExecuteParams(OperationType operationType, String entity, JsonObject jsonObject) {
            Objects.requireNonNull(operationType);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(jsonObject);
            this.operationType = operationType;
            this.entity = entity;
            this.jsonObject = jsonObject;
        }
    }

    enum OperationType {
        UPSERT, DELETE, DELETE_CHILD_RELATIONS
    }
}
