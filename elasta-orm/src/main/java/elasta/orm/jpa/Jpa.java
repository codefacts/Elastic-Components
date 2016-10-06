package elasta.orm.jpa;

import elasta.core.intfs.ConsumerUnchecked;
import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
public interface Jpa {

    <T> Class<T> getModelClass(String model);

    <T> Promise<JsonObject> find(Class<T> tClass, Object id);

    <T> Promise<List<JsonObject>> query(FunctionUnchecked<CriteriaBuilder, CriteriaQuery<T>> functionUnchecked);

    Promise<List<JsonArray>> queryArray(FunctionUnchecked<CriteriaBuilder, CriteriaQuery<Object[]>> functionUnchecked);

    <T> Promise<JsonObject> querySingle(FunctionUnchecked<CriteriaBuilder, CriteriaQuery<T>> functionUnchecked);

    Promise<JsonArray> querySingleArray(FunctionUnchecked<CriteriaBuilder, CriteriaQuery<Object[]>> functionUnchecked);

    <T> Promise<T> queryScalar(FunctionUnchecked<CriteriaBuilder, CriteriaQuery<T>> functionUnchecked);

    Promise<Void> update(FunctionUnchecked<CriteriaBuilder, CriteriaQuery> functionUnchecked);

    Promise<Void> update(List<FunctionUnchecked<CriteriaBuilder, CriteriaQuery>> functionUncheckedList);
}
