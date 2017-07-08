package tracker;

import com.google.common.collect.ImmutableMap;
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
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import tracker.entity_config.Entities;
import tracker.preprocessor.impl.DevicePreprocessorImpl;
import tracker.preprocessor.impl.PositionPreprocessorImpl;
import tracker.preprocessor.impl.UserPreprocessorImpl;

import java.util.Objects;

/**
 * Created by sohan on 7/7/2017.
 */
public interface EntityToStateHandlersMapExporter extends ModuleExporter {

    static ModuleSystemBuilder exportTo(ModuleSystemBuilder builder) {

        Objects.requireNonNull(builder);

        builder.export(EntityToStateHandlersMap.class, module -> {

            ImmutableMap.Builder<String, StateHandlersMap> mapBuilder = ImmutableMap.builder();

            addUpdatePreprocessor(mapBuilder, Entities.USER, new UserPreprocessorImpl(
                module.require(QueryExecutor.class),
                module.require(App.Config.class)
            ));

            addUpdatePreprocessor(mapBuilder, Entities.DEVICE, new DevicePreprocessorImpl(module.require(App.Config.class)));

            addUpdatePreprocessor(mapBuilder, Entities.POSITION, new PositionPreprocessorImpl());

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
