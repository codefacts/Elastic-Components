package elasta.orm.jpa;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
public interface Jpa {

    <T> Class<T> getModelClass(String model);

    <T> Promise<JsonObject> find(Class<T> tClass, Object id);

    Promise<List<JsonObject>> jpqlQuery(String jpql);

    Promise<List<JsonObject>> jpqlQuery(String jpql, JsonArray params);

    Promise<JsonObject> jpqlQuerySingle(String jpql);

    Promise<JsonObject> jpqlQuerySingle(String jpql, JsonArray params);

    Promise<List<JsonArray>> jpqlQueryArray(String jpql);

    Promise<List<JsonArray>> jpqlQueryArray(String jpql, JsonArray params);

    Promise<JsonArray> jpqlQuerySingleArray(String jpql);

    Promise<JsonArray> jpqlQuerySingleArray(String jpql, JsonArray params);

    <T> Promise<T> jpqlQueryScalar(String jpql, Class<T> tClass);

    <T> Promise<T> jpqlQueryScalar(String jpql, Class<T> tClass, JsonArray params);

    <T> Promise<List<JsonObject>> query(Fun1Unckd<CriteriaBuilder, CriteriaQuery<T>> fun1Unckd);

    Promise<List<JsonArray>> queryArray(Fun1Unckd<CriteriaBuilder, CriteriaQuery<Object[]>> fun1Unckd);

    <T> Promise<JsonObject> querySingle(Fun1Unckd<CriteriaBuilder, CriteriaQuery<T>> fun1Unckd);

    Promise<JsonArray> querySingleArray(Fun1Unckd<CriteriaBuilder, CriteriaQuery<Object[]>> fun1Unckd);

    <T> Promise<T> queryScalar(Fun1Unckd<CriteriaBuilder, CriteriaQuery<T>> fun1Unckd);

    Promise<Void> update(Fun1Unckd<CriteriaBuilder, CriteriaQuery> fun1Unckd);

    Promise<Void> update(List<Fun1Unckd<CriteriaBuilder, CriteriaQuery>> fun1UnckdList);
}
