package elasta.orm;

import elasta.core.promise.intfs.Promise;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.sql.core.Order;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 9/14/2016.
 */
public interface Orm {

    Promise<Long> countDistinct(String entity);

    Promise<Long> countDistinct(String entity, String alias, JsonObject criteria);

    <T> Promise<JsonObject> findOne(String entity, String alias, T id, Collection<FieldExpression> selections);

    <T> Promise<List<JsonObject>> findAll(String entity, String alias, Collection<T> ids, Collection<FieldExpression> selections);

    Promise<List<JsonObject>> findAll(String entity, String alias, JsonObject criteria, Collection<FieldExpression> selections);

    Promise<List<JsonObject>> findAll(QueryExecutor.QueryParams params);

    Promise<List<JsonArray>> queryArray(QueryExecutor.QueryArrayParams params);

    <T> Promise<T> querySingle(QueryExecutor.QueryArrayParams params);

    Promise<JsonObject> upsert(String entity, JsonObject data);

    <T extends Collection<JsonObject>> Promise<T> upsertAll(String entity, T jsonObjects);

    <T> Promise<T> delete(String entity, T id);

    <T, R extends Collection<T>> Promise<R> deleteAll(String entity, R ids);

    Promise<JsonObject> deleteChildRelations(String entity, JsonObject jsonObject);

    <T extends Collection<JsonObject>> Promise<T> deleteAllChildRelations(String entity, T jsonObjects);

    Promise<Void> execute(BaseOrm.ExecuteParams params);

    Promise<Void> executeAll(Collection<BaseOrm.ExecuteParams> paramss);
}
