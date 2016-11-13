package elasta.composer.module_exporter;

import elasta.core.flow.Flow;
import elasta.module.ModuleSystem;

import static elasta.core.flow.Flow.next;
import static elasta.core.flow.Flow.on;

/**
 * Created by Jango on 11/13/2016.
 */
public class ComposerExporterImpl implements ComposerExporter {

    @Override
    public void export(ModuleSystem moduleSystem) {
        moduleSystem.export(Flow.class, Flows.EVENT_HANDLER_FLOW, module -> {

            module.export(
                Flow.builder()
                    .when(States.START, next(States.INITIAL_TRANSFORMATION))
                    .when(States.INITIAL_TRANSFORMATION, next(States.CONVERTION))
                    .when(States.CONVERTION,
                        on(Events.CONVERTION_ERROR, States.CONVERTION_ERROR),
                        next(States.VALIDATION)
                    )
                    .when(States.CONVERTION_ERROR, next(States.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION))
                    .when(States.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION, next(States.VALIDATION_ERROR))
                    .when(States.VALIDATION,
                        on(Events.VALIDATION_ERROR, States.VALIDATION_ERROR),
                        next(States.TRANSFORMATION)
                    )
                    .when(States.VALIDATION_ERROR, next(States.END))
                    .when(States.TRANSFORMATION, next(States.ACTION))
                    .when(States.ACTION, next(States.RESULT_PREPERATION))
                    .when(States.RESULT_PREPERATION, next(States.END))
                    .when(States.END, Flow.end())
                    .initialState(States.START)

                    .build()
            );
        });
    }
}
