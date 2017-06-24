package tracker.impl;

import elasta.module.Module;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.BaseOrm;
import elasta.orm.Orm;
import elasta.orm.OrmExporter;
import elasta.orm.QueryDataLoader;
import elasta.orm.builder.impl.OperationMapBuilder;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.impl.EntityMappingHelperImpl;
import elasta.orm.impl.BaseOrmImpl;
import elasta.orm.impl.OrmImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import tracker.App;
import tracker.AppUtils;
import tracker.entity_config.Entities;

import java.util.Objects;

/**
 * Created by sohan on 6/25/2017.
 */
final public class AppImpl implements App {
    final Vertx vertx = Vertx.vertx();

    @Override
    public App start(Config config) {
        Objects.requireNonNull(config);

        final JDBCClient jdbcClient = JDBCClient.createShared(vertx, AppUtils.toJsonConfig(config.getDb()));

        final ModuleSystemBuilder builder = ModuleSystem.builder();

        builder.export(Config.class, module -> module.export(config));

        builder.export(JDBCClient.class, module -> module.export(jdbcClient));

        exportOrm(builder, jdbcClient);

        ModuleSystem moduleSystem = builder.build();



        return this;
    }

    private void exportOrm(ModuleSystemBuilder builder, JDBCClient jdbcClient) {

        OrmExporter.exportTo(
            OrmExporter.ExportToParams.builder()
                .entities(Entities.entities())
                .moduleSystemBuilder(builder)
                .jdbcClient(jdbcClient)
                .build()
        );
    }

    @Override
    public App stop() {
        return this;
    }
}
