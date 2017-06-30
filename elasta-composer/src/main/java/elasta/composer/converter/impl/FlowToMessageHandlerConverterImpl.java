package elasta.composer.converter.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.Msg;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.impl.ContextImpl;
import elasta.composer.impl.HeadersImpl;
import elasta.composer.impl.VertxMultiMap;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.model.request.UserModel;
import elasta.composer.converter.UserIdConverter;
import elasta.core.flow.Flow;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.Objects;

/**
 * Created by sohan on 6/29/2017.
 */
final public class FlowToMessageHandlerConverterImpl<T> implements FlowToMessageHandlerConverter<T> {
    final MessageProcessingErrorHandler messageProcessingErrorHandler;
    final UserIdConverter userIdConverter;
    final ConvertersMap convertersMap;

    public FlowToMessageHandlerConverterImpl(MessageProcessingErrorHandler messageProcessingErrorHandler, UserIdConverter userIdConverter, ConvertersMap convertersMap) {
        Objects.requireNonNull(messageProcessingErrorHandler);
        Objects.requireNonNull(userIdConverter);
        Objects.requireNonNull(convertersMap);
        this.messageProcessingErrorHandler = messageProcessingErrorHandler;
        this.userIdConverter = userIdConverter;
        this.convertersMap = convertersMap;
    }

    @Override
    public MessageHandler<T> convert(Flow flow) {

        return tMessage -> {

            try {

                flow.<Msg>start(
                    Msg.<T>builder()
                        .context(new ContextImpl(ImmutableMap.of()))
                        .headers(new HeadersImpl(
                            ComposerUtils.toListMultimap(tMessage.headers()),
                            convertersMap.getMap()
                        ))
                        .userId(userIdConverter.convert(tMessage.headers().get(UserModel.userId)))
                        .body(tMessage.body())
                        .build())
                    .then(msg -> tMessage.reply(msg.body(), new DeliveryOptions().setHeaders(new VertxMultiMap(msg.headers().getMultimap()))))
                    .err(throwable -> {
                        messageProcessingErrorHandler.onError(
                            new MessageProcessingErrorHandler.Params(
                                throwable,
                                tMessage
                            )
                        );
                    });

            } catch (Exception ex) {

                messageProcessingErrorHandler.onError(
                    new MessageProcessingErrorHandler.Params(
                        ex,
                        tMessage
                    )
                );
            }
        };
    }
}
