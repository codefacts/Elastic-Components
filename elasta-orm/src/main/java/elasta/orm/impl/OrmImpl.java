package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.orm.BaseOrm;
import elasta.orm.Orm;
import elasta.orm.OrmUtils;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sohan on 3/22/2017.
 */
final public class OrmImpl implements Orm {
    @Override
    public Promise<Long> count(String entity) {
        return null;
    }

    @Override
    public Promise<Long> count(String entity, String alias, JsonObject criteria) {
        return null;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String entity, T id) {
        return null;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String entity, String alias, T id, Collection<FieldExpression> selections) {
        return null;
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String entity, Collection<T> ids) {
        return null;
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String entity, String alias, Collection<T> ids, Collection<FieldExpression> selections) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String entity, String alias, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String entity, String alias, JsonObject criteria, Collection<FieldExpression> selections) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(QueryExecutor.QueryParams params) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll() {
        return null;
    }

    @Override
    public Promise<List<JsonArray>> queryArray(QueryExecutor.QueryArrayParams params) {
        return null;
    }

    @Override
    public <T> Promise<T> querySingle(QueryExecutor.QueryArrayParams params) {
        return null;
    }

    @Override
    public Promise<JsonObject> upsert(String entity, JsonObject data) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> upsertAll(String entity, Collection<JsonObject> jsonObjects) {
        return null;
    }

    @Override
    public <T> Promise<T> delete(String entity, T id) {
        return null;
    }

    @Override
    public <T> Promise<List<T>> deleteAll(String entity, Collection<T> ids) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> deleteChildRelations(String entity, JsonObject jsonObject) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> deleteAllChildRelations(String entity, Collection<JsonObject> jsonObjects) {
        return null;
    }

    @Override
    public Promise<Void> execute(BaseOrm.ExecuteParams params) {
        return null;
    }

    @Override
    public Promise<Void> executeAll(Collection<BaseOrm.ExecuteParams> paramss) {
        return null;
    }
}
