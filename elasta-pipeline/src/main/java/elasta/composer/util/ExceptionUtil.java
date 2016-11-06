package elasta.composer.util;

import elasta.core.intfs.CallableUnckd;
import elasta.core.intfs.RunnableUnckd;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by someone on 26-Jul-2015.
 */
final public class ExceptionUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtil.class);
    private static final String DEV_MODE = "dev-mode";

    public static void toRuntime(final RunnableUnckd runnableUnckd) {
        try {
            runnableUnckd.run();
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static <T> T toRuntimeCall(final CallableUnckd<T> runnable) {
        try {
            return runnable.call();
        } catch (final Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    public static void sallowRun(final RunnableUnckd runnableUnckd) {
        try {
            runnableUnckd.run();
        } catch (final Throwable e) {
            logSallowEx(e);
        }
    }

    public static <T> T sallowCall(final CallableUnckd<T> runnable) {
        try {
            return runnable.call();
        } catch (final Throwable e) {
            logSallowEx(e);
        }
        return null;
    }

    public static void fail(final Message message, final Throwable throwable) {
        Objects.requireNonNull(message, "ExceptionUtil.fail: argument message can't be null.");
        Objects.requireNonNull(throwable, "ExceptionUtil.fail: argument throwable can't be null.");

        final String uuid = UUID.randomUUID().toString();

        message.fail(ErrorCodes.SERVER_ERROR.code(), errorMessage(ErrorCodes.SERVER_ERROR.code(), message, throwable, uuid));
        LOGGER.error("FAILING_MESSAGE: " +
            new JsonObject()
                .put("uuid", uuid)
                .put("address", message.address())
                .put("body", message.body())
                .put("headers", message.headers().toString()).encodePrettily(), throwable);
    }

    private static String errorMessage(int code, Message message, Throwable throwable, String uuid) {
        if (System.getProperty(DEV_MODE) != null) {
            return
                new JsonObject()
                    .put("exception", throwable.getClass().toString())
                    .put("cause", throwable.getCause() == null ? "" : throwable.getCause().toString())
                    .put("message", throwable.getMessage())
                    .put("code", code)
                    .put("uuid", uuid)
                    .put("address", message.address())
                    .put("body", message.body())
                    .put("headers", message.headers().toString()).encodePrettily();
        } else {
            return "Server Error. Error code: " + code + ", uuid: " + uuid + "";
        }
    }

    public static void logSallowEx(final Throwable e) {
        LOGGER.error("EXCEPTION_SALLOWED: ", e);
    }

    public static void main(String... args) {
        MultiMap entries = MultiMap.caseInsensitiveMultiMap();
        System.out.println(entries.add("kk", "kk")
            .add("kk", "tt")
            .add("ss", "ss"));
    }
}
