package elasta.composer.pipeline.validator.composer;

import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.impl.*;
import elasta.composer.pipeline.validator.impl.type.*;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by shahadat on 2/28/16.
 */
public class FieldValidatorComposer {
    private final String field;
    private final List<JsonObjectValidator> validatorList;

    public FieldValidatorComposer(String field, List<JsonObjectValidator> validatorList) {
        this.field = field;
        this.validatorList = validatorList;
    }

    public FieldValidatorComposer numberType() {
        validatorList.add(new NumberValidator(field));
        return this;
    }

    public FieldValidatorComposer booleanType() {
        validatorList.add(new BooleanValidator(field));
        return this;
    }

    public FieldValidatorComposer integerType() {
        validatorList.add(new IntegerValidator(field));
        return this;
    }

    public FieldValidatorComposer longType() {
        validatorList.add(new LongValidator(field));
        return this;
    }

    public FieldValidatorComposer positive() {
        validatorList.add(new PositiveValidator(field));
        return this;
    }

    public FieldValidatorComposer nonZero() {
        validatorList.add(new NonZeroValidator(field));
        return this;
    }

    public FieldValidatorComposer floatType() {
        validatorList.add(new FloatValidator(field));
        return this;
    }

    public FieldValidatorComposer doubleType() {
        validatorList.add(new DoubleValidator(field));
        return this;
    }

    public FieldValidatorComposer stringType() {
        validatorList.add(new StringValidator(field));
        return this;
    }

    public FieldValidatorComposer doubleRange() {
        validatorList.add(new DoubleValidator(field));
        return this;
    }

    public FieldValidatorComposer email() {
        validatorList.add(new EmailValidator(field));
        return this;
    }

    public FieldValidatorComposer length(int minLength, int maxLength) {
        validatorList.add(new LengthValidator(field, minLength, maxLength));
        return this;
    }

    public FieldValidatorComposer maxLength(int maxLength) {
        validatorList.add(new MaxLengthValidator(field, maxLength));
        return this;
    }

    public FieldValidatorComposer minLength(int minLength) {
        validatorList.add(new MinLengthValidator(field, minLength));
        return this;
    }

    public FieldValidatorComposer notNullEmptyOrWhiteSpace() {
        validatorList.add(new NotNullEmptyOrWhiteSpaceValidator(field));
        return this;
    }

    public FieldValidatorComposer notNull() {
        validatorList.add(new NotNullValidator(field));
        return this;
    }

    public FieldValidatorComposer pattern(Pattern pattern) {
        validatorList.add(new PatternValidator(field, pattern));
        return this;
    }

    public FieldValidatorComposer phone() {
        validatorList.add(new PhoneValidator(field));
        return this;
    }

    public FieldValidatorComposer range(long min, long max) {
        validatorList.add(new RangeValidator(field, min, max));
        return this;
    }

}
