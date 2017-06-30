package elasta.composer.state.handlers.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationSuccessModelBuilder;
import elasta.composer.state.handlers.builder.ValidateAllStateHandlerBuilder;
import elasta.composer.state.handlers.impl.ValidateAllStateHandlerImpl;
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
 * Created by sohan on 5/21/2017.
 */
final public class ValidateAllStateHandlerBuilderImpl implements ValidateAllStateHandlerBuilder {
    final String entity;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final ValidationSuccessModelBuilder validationSuccessModelBuilder;

    public ValidateAllStateHandlerBuilderImpl(String entity, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ValidationSuccessModelBuilder validationSuccessModelBuilder) {
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
    public MsgEnterEventHandlerP<JsonArray, Json> build() {

        return new ValidateAllStateHandlerImpl(
            entity,
            jsonObjectValidatorAsync,
            validationErrorModelBuilder,
            validationSuccessModelBuilder
        );
    }
}
