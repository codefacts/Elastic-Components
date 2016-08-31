package elasta.composer.pipelines;

import io.crm.promise.Promises;
import io.crm.promise.intfs.Defer;
import io.crm.promise.intfs.Promise;
import io.crm.util.ExceptionUtil;
import io.crm.util.Util;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

import java.util.Objects;

/**
 * Created by shahadat on 3/2/16.
 */
final public class PipelineUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineUtils.class);

    private static final String $$$SEQ_CONTINUE = "$$$SEQ_CONTINUE";

    public static <T> rx.Observable<Message<T>> bridgeAndInitiate(final EventBus eventBus, final String dest,
                                                                  Object message, DeliveryOptions deliveryOptions) {

        final Observable<Message<T>> emitter = Observable.create(
            subscriber -> {

                eventRequestLoop(eventBus, dest, message, deliveryOptions, subscriber, null);
            });

        return emitter;
    }

    private static <T> void eventRequestLoop(final EventBus eventBus, final String dest, Object message,
                                             DeliveryOptions deliveryOptions,
                                             Subscriber<? super Message<T>> subscriber,
                                             DeliveryOptions replyDeliveryOptions) {
        Objects.requireNonNull(deliveryOptions);
        Objects.requireNonNull(replyDeliveryOptions);

        Util.<T>send(eventBus, dest, message, deliveryOptions)
            .complete(p -> {
                if (!subscriber.isUnsubscribed()) {
                    if (p.isSuccess()) {
                        subscriber.onNext(p.get());
                    } else {
                        subscriber.onError(p.error());
                    }
                }
            })
            .filter(msg -> msg.headers().contains($$$SEQ_CONTINUE) && !subscriber.isUnsubscribed())
            .then(msg -> reqLoop(msg, deliveryOptions, subscriber, replyDeliveryOptions));
    }

    private static <T> void reqLoop(Message<T> msg, DeliveryOptions deliveryOptions,
                                    Subscriber<? super Message<T>> subscriber, DeliveryOptions replyDeliveryOptions) {
        if (!subscriber.isUnsubscribed()) {
            msg.<T>reply(null, replyDeliveryOptions, asyncResult -> {


                if (!subscriber.isUnsubscribed()) {
                    if (msg.headers().contains($$$SEQ_CONTINUE)) {
                        if (asyncResult.succeeded()) {
                            subscriber.onNext(asyncResult.result());
                            reqLoop(msg, deliveryOptions, subscriber, replyDeliveryOptions);
                        } else {
                            subscriber.onError(asyncResult.cause());
                        }
                    } else {
                        subscriber.onCompleted();
                    }
                }

            });
        }
    }

    public static <T> void bridgeFrom(Message message, Observable<T> src, DeliveryOptions replyDeliveryOptions) {
        Objects.requireNonNull(replyDeliveryOptions);

        BridgeSub<T> bridgeSub = new BridgeSub<>();

        bridgeSub.observer = new Observer<T>() {
            @Override
            public void onCompleted() {
                replyDeliveryOptions.getHeaders().remove($$$SEQ_CONTINUE);
                message.reply(null, replyDeliveryOptions);
            }

            @Override
            public void onError(Throwable e) {
                replyDeliveryOptions.getHeaders().remove($$$SEQ_CONTINUE);
                ExceptionUtil.fail(message, e);
            }

            @Override
            public void onNext(T t) {
                replyDeliveryOptions.getHeaders().add($$$SEQ_CONTINUE, "");
                onNextLoop(t, message, replyDeliveryOptions, bridgeSub);
            }
        };

        bridgeSub.subscription = src.subscribe(bridgeSub);
    }

    private static <T> void onNextLoop(T t, Message message, DeliveryOptions replyDeliveryOptions, BridgeSub<T> bridgeSub) {
        final Defer<Message> defer = Promises.defer();
        message.reply(t, replyDeliveryOptions, Util.makeDeferred(defer));

        MyObserver<T> myObserver = new MyObserver<>();
        bridgeSub.observer = myObserver;

        Promises.when(myObserver.promise(), defer.promise())
            .then(tpl2 -> tpl2.accept((tNotification, msg) -> {
                switch (tNotification.type) {
                    case Notification.COMPLETE:
                        replyDeliveryOptions.getHeaders().remove($$$SEQ_CONTINUE);
                        message.reply(null, replyDeliveryOptions);
                        break;
                    case Notification.ERROR:
                        replyDeliveryOptions.getHeaders().remove($$$SEQ_CONTINUE);
                        ExceptionUtil.fail(message, tNotification.e);
                        break;
                    case Notification.NEXT:
                        onNextLoop(tNotification.val, msg, replyDeliveryOptions, bridgeSub);
                        break;
                }
            }))
            .error(e -> LOGGER.error("PIPELINES_ERROR", e));
    }

    private static final class MyObserver<T> implements Observer<T> {
        private final Defer<Notification<T>> defer = Promises.defer();

        Promise<Notification<T>> promise() {
            return defer.promise();
        }

        @Override
        public void onCompleted() {
            defer.complete(new Notification<T>(null, null, Notification.COMPLETE));
        }

        @Override
        public void onError(Throwable e) {
            defer.complete(new Notification<T>(null, e, Notification.ERROR));
        }

        @Override
        public void onNext(T t) {
            defer.complete(new Notification<T>(t, null, Notification.NEXT));
        }
    }

    private static final class Notification<T> {
        public final T val;
        public final Throwable e;
        public final int type;

        private Notification(T val, Throwable e, int type) {
            this.val = val;
            this.e = e;
            this.type = type;
        }

        public static final int ERROR = 1;
        public static final int COMPLETE = 2;
        public static final int NEXT = 3;
    }

    private static final class BridgeSub<T> extends Subscriber<T> {
        private Subscription subscription;
        Observer<T> observer;

        @Override
        public void onCompleted() {
            subscription.unsubscribe();
            observer.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            subscription.unsubscribe();
            observer.onError(e);
        }

        @Override
        public void onNext(T t) {
            observer.onNext(t);
        }
    }
}
