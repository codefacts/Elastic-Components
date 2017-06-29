package tracker.impl;

import elasta.composer.state.handlers.UserIdConverter;

/**
 * Created by sohan on 6/29/2017.
 */
final public class UserIdConverterImpl implements UserIdConverter {
    @Override
    public Object convert(Object userId) {
        return userId;
    }
}
