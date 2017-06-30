package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationSuccessModelBuilder;
import elasta.composer.state.handlers.ValidateAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.pipeline.validator.ValidationResult;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class ValidateAllStateHandlerImpl implements ValidateAllStateHandler<JsonArray, Json> {
    final String entity;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final ValidationSuccessModelBuilder validationSuccessModelBuilder;

    public ValidateAllStateHandlerImpl(String entity, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ValidationSuccessModelBuilder validationSuccessModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        Objects.requireNonNull(validationSuccessModelBuilder);
        this.entity = entity;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
        this.validationSuccessModelBuilder = validationSuccessModelBuilder;
    }

    @Override
    public Promise<StateTrigger<Msg<Json>>> handle(Msg<JsonArray> msg) throws Throwable {

        List<Promise<List<ValidationResult>>> promiseList = msg.body().stream().map(o -> ((JsonObject) o)).map(jsonObjectValidatorAsync::validate).collect(Collectors.toList());

        Promise<StateTrigger<Msg>> promise = Promises.when(
            promiseList
        ).map(lists -> {

            ImmutableList.Builder<JsonObject> builder = ImmutableList.builder();

            boolean pass = true;

            for (List<ValidationResult> validationErrors : lists) {

                if (validationErrors.size() > 0) {

                    builder.add(
                        validationErrorModelBuilder.build(
                            ValidationErrorModelBuilder.BuildParams.builder()
                                .entity(entity)
                                .validationErrors(validationErrors)
                                .build()
                        )
                    );

                    pass = false;

                } else {

                    builder.add(
                        validationSuccessModelBuilder.build(
                            ValidationSuccessModelBuilder.BuildParams.builder().build()
                        )
                    );

                }

            }

            if (Utils.not(pass)) {

                return Flow.trigger(
                    Events.validationError,
                    msg.withBody(
                        validationErrorModelBuilder.build(
                            ValidationErrorModelBuilder.BuildParams.builder().entity(entity).validationErrors(Collections.emptyList()).build()
                        )
                    )
                );
            }

            return Flow.trigger(Events.next, msg);

        });

        return typecast(promise);
    }

    private Promise<StateTrigger<Msg<Json>>> typecast(Promise promise) {
        return promise;
    }
    
}
