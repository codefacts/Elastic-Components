package elasta.composer.message.handlers;

/**
 * Created by sohan on 6/29/2017.
 */
public interface MessageHandlerBuilder<T> {
    MessageHandler<T> build();
}
