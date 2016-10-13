package jpatest.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import elasta.module.ModuleSystem;
import elasta.orm.Db;
import elasta.orm.OrmExporter;
import elasta.orm.json.insert_or_update.ObjectData;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import jpatest.models.Br;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.PluralAttribute;

/**
 * Created by Jango on 10/12/2016.
 */
public class Test {
    private static final io.vertx.core.json.JsonObject DB_CONFIG = new JsonObject("{\n" +
        "  \"url\": \"jdbc:mysql://localhost:3306/jpadb\",\n" +
        "  \"user\": \"root\",\n" +
        "  \"password\": \"\",\n" +
        "  \"driver_class\": \"com.mysql.jdbc.Driver\"\n" +
        "}");

    public static void main(String[] args) throws InterruptedException {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Main.PU);

        EntityManager em = emf.createEntityManager();

        try {

            ModuleSystem moduleSystem = ModuleSystem.create();

            OrmExporter.exportModule(moduleSystem);

            moduleSystem.export(EntityManagerFactory.class, module -> module.export(emf));
            moduleSystem.export(Vertx.class, module -> module.export(Vertx.vertx()));
            moduleSystem.export(JDBCClient.class, module -> module.export(JDBCClient.createShared(module.require(Vertx.class), DB_CONFIG)));
            moduleSystem.export(ObjectMapper.class, module -> module.export(new ObjectMapper()));

            Db db = moduleSystem.require(Db.class);

            db.count("Br").then(aLong -> System.out.println("count: " + aLong));

            System.out.println("DB created.");

            Thread.sleep(Long.MAX_VALUE);

        } finally {

            em.close();

            emf.close();
        }
    }
}
