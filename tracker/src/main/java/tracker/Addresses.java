package tracker;

import tracker.entity_config.Entities;

import java.util.Objects;

/**
 * Created by sohan on 6/29/2017.
 */
public interface Addresses {

    String authenticate = "authenticate";
    String findAllPositionsGroupByUserId = findAll(Entities.POSITION) + ".group-by-user-id";

    static String post(String address) {
        Objects.requireNonNull(address);
        return "post." + address;
    }

    static String add(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".add";
    }

    static String addAll(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".add.all";
    }

    static String update(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".update";
    }

    static String updateAll(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".update.all";
    }

    static String delete(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".delete";
    }

    static String deleteAll(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".delete.all";
    }

    static String findOne(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".find.one";
    }

    static String findAll(String entity) {
        Objects.requireNonNull(entity);
        return entity + ".find.all";
    }
}
