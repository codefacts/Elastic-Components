package elasta.orm;

import com.fasterxml.jackson.databind.ObjectMapper;
import elasta.module.ModuleSystem;
import elasta.orm.jpa.*;
import elasta.orm.json.sql.DbSql;
import elasta.orm.json.sql.DbSqlImpl;
import elasta.orm.json.sql.SqlBuilderUtils;
import elasta.orm.json.sql.SqlBuilderUtilsImpl;
import elasta.orm.json.sql.criteria.SqlCriteriaTranslator;
import elasta.orm.json.sql.criteria.SqlCriteriaTranslatorImpl;
import elasta.orm.json.sql.criteria.SqlMaps;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * Created by Jango on 9/14/2016.
 */
public interface OrmExporter {
    String DB_CONFIG = "db-config";
    String ENTITY_CLASS_MAP = "entityClassMap";
    String FUNCTION_MAP = "functionMap";
    String SYMBOL_TRANSLATOR_MAP = "symbolTranslatorMap";

    void export(ModuleSystem moduleSystem);

    static OrmExporter get() {
        return OrmExporter::exportModule;
    }

    static void exportModule(ModuleSystem moduleSystem) {
        moduleSystem.export(JDBCClient.class,
            module -> module.export(JDBCClient.createShared(
                module.require(Vertx.class), module.require(JsonObject.class, DB_CONFIG))
            ));

        moduleSystem.export(Db.class, module -> module.export(new DbImpl(
            module.require(Jpa.class), module.require(DbSql.class), module.require(ModelInfoProvider.class),
            module.require(FindExistingIds.class), module.require(SqlCriteriaTranslator.class)
        )));

        moduleSystem.export(Jpa.class, module -> module.export(new JpaImpl(
            module.require(Vertx.class), module.require(EntityManagerFactory.class),
            module.require(ObjectMapper.class), module.require(Map.class, ENTITY_CLASS_MAP)
        )));

        moduleSystem.export(Map.class, ENTITY_CLASS_MAP,
            module -> module.export(JpaUtils.entityClassMap(module.require(EntityManagerFactory.class))));

        moduleSystem.export(DbSql.class, module -> {
            module.export(new DbSqlImpl(module.require(JDBCClient.class), module.require(SqlBuilderUtils.class)));
        });

        moduleSystem.export(SqlBuilderUtils.class, module -> module.export(new SqlBuilderUtilsImpl()));

        moduleSystem.export(ModelInfoProvider.class, module -> module.export(new ModelInfoProviderImpl(
            JpaUtils.modelInfoByModelMap(
                module.require(EntityManagerFactory.class)
            ))));

        moduleSystem.export(FindExistingIds.class, module -> module.export(new FindExistingIdsImpl(
            module.require(ModelInfoProvider.class), module.require(DbSql.class)
        )));

        moduleSystem.export(SqlCriteriaTranslator.class, module -> module.export(new SqlCriteriaTranslatorImpl(
            module.require(Map.class, FUNCTION_MAP), module.require(Map.class, SYMBOL_TRANSLATOR_MAP)
        )));

        moduleSystem.export(Map.class, FUNCTION_MAP, module -> module.export(SqlMaps.getFunctionMap()));

        moduleSystem.export(Map.class, SYMBOL_TRANSLATOR_MAP, module -> module.export(SqlMaps.getSymbolTranslatorMap()));
    }
}
