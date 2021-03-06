package tracker;

import com.google.common.collect.ImmutableMap;
import elasta.composer.AppDateTimeFormatter;
import elasta.composer.EntityToStateHandlersMap;
import elasta.composer.StateHandlersMap;
import elasta.composer.impl.EntityToStateHandlersMapImpl;
import elasta.composer.impl.StateHandlersMapImpl;
import elasta.composer.state.handlers.BeforeAddAllStateHandler;
import elasta.composer.state.handlers.BeforeAddStateHandler;
import elasta.composer.state.handlers.impl.BeforeAddAllStateHandlerImpl;
import elasta.composer.state.handlers.impl.BeforeAddStateHandlerImpl;
import elasta.core.intfs.Fun2Async;
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.query.QueryExecutor;
import tracker.entity_config.Entities;
import tracker.preprocessor.impl.DevicePreprocessorImpl;
import tracker.preprocessor.impl.PositionPreprocessorImpl;
import tracker.preprocessor.impl.UserPreprocessorImpl;
import tracker.preprocessor.merchandiser.impl.OutletPreprocessorImpl;

import java.util.Objects;

/**
 * Created by sohan on 7/7/2017.
 */
public interface EntityToStateHandlersMapExporter extends ModuleExporter {

    static ModuleSystemBuilder exportTo(ModuleSystemBuilder builder) {

        Objects.requireNonNull(builder);

        builder.export(EntityToStateHandlersMap.class, module -> {

            ImmutableMap.Builder<String, StateHandlersMap> mapBuilder = ImmutableMap.builder();

            addUpdatePreprocessor(mapBuilder, Entities.OUTLET_ENTITY, new OutletPreprocessorImpl());

            addUpdatePreprocessor(mapBuilder, Entities.USER_ENTITY, new UserPreprocessorImpl(
                module.require(QueryExecutor.class),
                module.require(App.Config.class)
            ));

            addUpdatePreprocessor(mapBuilder, Entities.DEVICE_ENTITY, new DevicePreprocessorImpl(module.require(App.Config.class)));

            addUpdatePreprocessor(mapBuilder, Entities.POSITION_ENTITY, new PositionPreprocessorImpl(module.require(AppDateTimeFormatter.class)));

            module.export(
                new EntityToStateHandlersMapImpl(mapBuilder.build())
            );
        });

        return builder;
    }

    static void addUpdatePreprocessor(ImmutableMap.Builder<String, StateHandlersMap> mapBuilder, String entity, Fun2Async fun2Async) {
        mapBuilder.put(entity, new StateHandlersMapImpl(
            ImmutableMap.of(
                BeforeAddStateHandler.class, new BeforeAddStateHandlerImpl(fun2Async),
                BeforeAddAllStateHandler.class, new BeforeAddAllStateHandlerImpl(fun2Async)
            )
        ));
    }
}
