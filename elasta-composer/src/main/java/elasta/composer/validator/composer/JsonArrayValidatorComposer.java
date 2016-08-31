package elasta.composer.validator.composer;

import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class JsonArrayValidatorComposer {
    private final List<Validator<JsonObject>> validatorList;

    public JsonArrayValidatorComposer(List<Validator<JsonObject>> validatorList) {
        this.validatorList = validatorList;
    }


}
