package tracker.server.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.sql.SqlExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import tracker.server.StorageMap;
import tracker.server.ex.StorageMapException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 7/3/2017.
 */
final public class StorageMapImpl implements StorageMap {
    final String dateFormat = "yyyy-MM-dd HH:mm:ss";
    final String tableName;
    final SqlExecutor sqlExecutor;

    public StorageMapImpl(String tableName, SqlExecutor sqlExecutor, Vertx vertx, long delay) {
        Objects.requireNonNull(tableName);
        Objects.requireNonNull(sqlExecutor);
        this.tableName = tableName;
        this.sqlExecutor = sqlExecutor;
        scheduleDeleteQuery(vertx, delay);
    }

    @Override
    public <T> Promise<Void> put(String key, T value, Date expireTime) {
        return putData(key, value, expireTime);
    }

    @Override
    public Promise<Optional<JsonObject>> getJsonObject(String key) {
        return getValue(key, Type.jsonObjectType);
    }

    @Override
    public Promise<Optional<JsonArray>> getJsonArray(String key) {
        return getValue(key, Type.jsonArrayType);
    }

    @Override
    public Promise<Optional<Long>> getLong(String key) {
        return getValue(key, Type.longType);
    }

    @Override
    public Promise<Optional<Double>> getDouble(String key) {
        return getValue(key, Type.doubleType);
    }

    @Override
    public Promise<Optional<Boolean>> getBoolean(String key) {
        return getValue(key, Type.booleanType);
    }

    @Override
    public Promise<Optional<String>> getString(String key) {
        return getValue(key, Type.stringType);
    }

    @Override
    public Promise<Optional<Date>> getDate(String key) {
        return getValue(key, Type.dateType);
    }

    @Override
    public Promise<Boolean> containsKey(String key) {
        return _containsKey(key);
    }

    @Override
    public Promise<Void> remove(String key) {
        return _remove(key);
    }

    @Override
    public Promise<Long> count() {
        return _count();
    }

    private Promise<Long> _count() {
        return sqlExecutor.query("SELECT COUNT(`name`) FROM `StorageMap` WHERE 1")
            .map(resultSet -> resultSet.getResults().get(0).getLong(0));
    }

    private Promise<Void> _remove(String key) {
        return sqlExecutor.update("DELETE FROM `StorageMap` WHERE `name` = ?", new JsonArray(ImmutableList.of(key)));
    }

    private Promise<Boolean> _containsKey(String key) {
        return sqlExecutor.query("SELECT COUNT(`name`) FROM `StorageMap` WHERE `name` = ?", new JsonArray(ImmutableList.of(key)))
            .map(resultSet -> resultSet.getResults().get(0).getLong(0) > 0);
    }

    private void scheduleDeleteQuery(Vertx vertx, long delay) {
        vertx.setPeriodic(delay, id -> {
            sqlExecutor.update("DELETE FROM `StorageMap` WHERE `expireTime` < NOW()");
        });
    }

    private <T> Promise<Optional<T>> getValue(String key, int type) {
        return sqlExecutor.query("SELECT `value` FROM `StorageMap` WHERE `name` = ? and `type` = ?", new JsonArray(ImmutableList.of(
            key, type
        ))).map(resultSet -> {

            if (resultSet.getNumRows() <= 0) {
                return Optional.empty();
            }

            String value = resultSet.getResults().get(0).getString(0);

            return Optional.of(
                (T) convertToType(value, type)
            );
        });
    }

    private Object convertToType(String value, int type) throws Exception {
        if (type == Type.longType) {
            return Long.parseLong(value);
        } else if (type == Type.doubleType) {
            return Double.parseDouble(value);
        } else if (type == Type.booleanType) {
            return Boolean.parseBoolean(value);
        } else if (type == Type.stringType) {
            return value;
        } else if (type == Type.dateType) {
            return new SimpleDateFormat(dateFormat).parse(value);
        } else if (type == Type.jsonObjectType) {
            return new JsonObject(value);
        } else if (type == Type.jsonArrayType) {
            return new JsonArray(value);
        }
        throw new StorageMapException("Invalid type '" + type + "' provided");
    }

    private <T> Promise<Void> putData(String key, T value, Date expireTime) {
        if (value.getClass() == Byte.class || value.getClass() == Short.class || value.getClass() == Integer.class || value.getClass() == Long.class) {
            return addEntry(key, String.valueOf(value), expireTime, Type.longType);
        } else if (value.getClass() == Float.class || value.getClass() == Double.class) {
            return addEntry(key, String.valueOf(value), expireTime, Type.doubleType);
        } else if (value.getClass() == Boolean.class) {
            return addEntry(key, String.valueOf(value), expireTime, Type.booleanType);
        } else if (value.getClass() == String.class) {
            return addEntry(key, String.valueOf(value), expireTime, Type.stringType);
        } else if (value instanceof Date) {
            return addEntry(key, dateToStr(((Date) value)), expireTime, Type.dateType);
        } else if (value instanceof JsonObject) {
            return addEntry(key, jsonObjectToStr(value), expireTime, Type.jsonObjectType);
        } else if (value instanceof Map) {
            return addEntry(key, mapToStr(value), expireTime, Type.jsonObjectType);
        } else if (value instanceof JsonArray) {
            return addEntry(key, jsonArrayToStr(value), expireTime, Type.jsonArrayType);
        } else if (value instanceof List) {
            return addEntry(key, listToStr(value), expireTime, Type.jsonArrayType);
        } else {
            throw new StorageMapException("Type '" + value.getClass() + "' is not supported");
        }
    }

    private <T> String listToStr(T value) {
        return new JsonArray(((List) value)).encode();
    }

    private <T> String mapToStr(T value) {
        return new JsonObject(((Map<String, Object>) value)).encode();
    }

    private <T> String jsonArrayToStr(T value) {
        return ((JsonArray) value).encode();
    }

    private <T> String jsonObjectToStr(T value) {
        return ((JsonObject) value).encode();
    }

    private <T> String dateToStr(Date value) {

        return new SimpleDateFormat(dateFormat).format(value);
    }

    private <T> Promise<Void> addEntry(String key, String value, Date expireTime, int type) {

        return sqlExecutor.update("INSERT INTO `StorageMap` (`name`, `value`, `expireTime`, `type`) VALUES (?, ?, ?, ?)", new JsonArray(ImmutableList.of(
            key, value, expireTime, type
        )));
    }

    interface Type {
        int longType = 1;
        int doubleType = 2;
        int booleanType = 3;
        int stringType = 6;
        int dateType = 7;
        int jsonObjectType = 8;
        int jsonArrayType = 9;
    }

    public static void main(String[] asdfas) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";

        String format = new SimpleDateFormat(dateFormat).format(new Date());
        System.out.println(format);
    }
}
