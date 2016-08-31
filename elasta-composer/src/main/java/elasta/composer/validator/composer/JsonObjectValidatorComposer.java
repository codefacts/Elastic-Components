package elasta.composer.validator.composer;

import elasta.composer.validator.Validator;
import io.crm.MessageBundle;
import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by shahadat on 2/28/16.
 */
public class JsonObjectValidatorComposer {
    private final List<Validator<JsonObject>> validatorList;
    private final MessageBundle messageBundle;

    public JsonObjectValidatorComposer(List<Validator<JsonObject>> validatorList, MessageBundle messageBundle) {
        Objects.requireNonNull(validatorList);
        Objects.requireNonNull(messageBundle);
        this.validatorList = validatorList;
        this.messageBundle = messageBundle;
    }

    public JsonObjectValidatorComposer field(String field, Consumer<FieldValidatorComposer> consumer) {
        consumer.accept(new FieldValidatorComposer(field, validatorList, messageBundle));
        return this;
    }

    public List<Validator<JsonObject>> getValidatorList() {
        return validatorList;
    }
}
