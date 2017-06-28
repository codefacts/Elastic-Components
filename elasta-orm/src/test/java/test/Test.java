package test;

import com.google.common.collect.ImmutableMap;
import elasta.module.ModuleSystem;
import elasta.orm.BaseOrm;
import elasta.orm.Orm;
import elasta.orm.OrmExporter;
import elasta.orm.QueryDataLoader;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.impl.QueryDataLoaderImpl;
import elasta.sql.SqlDB;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by sohan on 3/22/2017.
 */
public interface Test {
    Vertx VERTX = Vertx.vertx();
    ModuleSystem MODULE_SYSTEM = moduleSystem(VERTX);

    static ModuleSystem moduleSystem(Vertx vertx) {
        return OrmExporter.exportTo(
            OrmExporter.ExportToParams.builder()
                .jdbcClient(jdbcClient("jpadb", vertx))
                .moduleSystemBuilder(ModuleSystem.builder())
                .isNewKey("$isNew")
                .entities(Employees.entities())
                .build()
        ).build();
    }

    static JDBCClient jdbcClient(String db, Vertx vertx) {
        return JDBCClient.createShared(vertx, new JsonObject(
            ImmutableMap.of(
                "user", "root",
                "password", "",
                "driver_class", "com.mysql.jdbc.Driver",
                "url", "jdbc:mysql://localhost/" + db
            )
        ));
    }

    static SqlDB sqlDB() {
        return MODULE_SYSTEM.require(SqlDB.class);
    }

    static BaseOrm baseOrm() {
        return MODULE_SYSTEM.require(BaseOrm.class);
    }

    static EntityMappingHelper helper() {
        return MODULE_SYSTEM.require(EntityMappingHelper.class);
    }

    static Orm orm() {
        return MODULE_SYSTEM.require(Orm.class);
    }

    static <T> T require(Class<T> tClass) {
        return MODULE_SYSTEM.require(tClass);
    }
}
