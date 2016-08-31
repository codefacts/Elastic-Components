package elasta.composer.validator.composer;

import io.crm.MessageBundle;
import io.crm.validator.NonZeroValidator;
import io.crm.validator.PositiveValidator;
import io.crm.validator.Validator;
import io.crm.validator.impl.*;
import io.crm.validator.impl.type.*;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by shahadat on 2/28/16.
 */
public class FieldValidatorComposer {
    private final String field;
    private final List<Validator<JsonObject>> validatorList;
    private final MessageBundle messageBundle;

    public FieldValidatorComposer(String field, List<Validator<JsonObject>> validatorList, MessageBundle messageBundle) {
        this.field = field;
        this.validatorList = validatorList;
        this.messageBundle = messageBundle;
    }

    public FieldValidatorComposer numberType() {
        validatorList.add(new NumberValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer booleanType() {
        validatorList.add(new BooleanValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer integerType() {
        validatorList.add(new IntegerValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer longType() {
        validatorList.add(new LongValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer positive() {
        validatorList.add(new PositiveValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer nonZero() {
        validatorList.add(new NonZeroValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer floatType() {
        validatorList.add(new FloatValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer doubleType() {
        validatorList.add(new DoubleValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer stringType() {
        validatorList.add(new StringValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer doubleRange() {
        validatorList.add(new DoubleValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer email() {
        validatorList.add(new EmailValidationError(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer length(int minLength, int maxLength) {
        validatorList.add(new LengthValidationError(messageBundle, field, minLength, maxLength));
        return this;
    }

    public FieldValidatorComposer maxLength(int maxLength) {
        validatorList.add(new MaxLengthValidationError(messageBundle, field, maxLength));
        return this;
    }

    public FieldValidatorComposer minLength(int minLength) {
        validatorList.add(new MinLengthValidationError(messageBundle, field, minLength));
        return this;
    }

    public FieldValidatorComposer notNullEmptyOrWhiteSpace() {
        validatorList.add(new NotNullEmptyOrWhiteSpace(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer notNull() {
        validatorList.add(new NotNullValidator(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer pattern(Pattern pattern) {
        validatorList.add(new PatternValidationError(messageBundle, field, pattern));
        return this;
    }

    public FieldValidatorComposer phone() {
        validatorList.add(new PhoneValidationError(messageBundle, field));
        return this;
    }

    public FieldValidatorComposer range(long min, long max) {
        validatorList.add(new RangeValidator(messageBundle, field, min, max));
        return this;
    }

}
