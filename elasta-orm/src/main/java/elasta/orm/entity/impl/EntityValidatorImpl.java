package elasta.orm.entity.impl;

import elasta.orm.entity.*;

/**
 * Created by sohan on 3/17/2017.
 */
final public class EntityValidatorImpl implements EntityValidator {
    @Override
    public void validate(Params params) {
        new InternalEntityValidator(params).validate();
    }

    public static void main(String[] afasdf) {
    }

}
