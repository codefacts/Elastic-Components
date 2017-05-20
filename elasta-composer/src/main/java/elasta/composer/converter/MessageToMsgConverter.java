package elasta.composer.converter;

import elasta.composer.Msg;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/20/2017.
 */
public interface MessageToMsgConverter<T> extends Converter<Message<T>, Msg<T>> {
    @Override
    Msg<T> convert(Message<T> message);
}
