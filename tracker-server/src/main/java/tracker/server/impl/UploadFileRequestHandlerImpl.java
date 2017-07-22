package tracker.server.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import tracker.server.request.handlers.RequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 7/22/2017.
 */
final public class UploadFileRequestHandlerImpl implements RequestHandler {
    final Vertx vertx;
    final RequestProcessingErrorHandler requestProcessingErrorHandler;
    final Map<String, String> uriToUploadDirectorMap;

    public UploadFileRequestHandlerImpl(Vertx vertx, RequestProcessingErrorHandler requestProcessingErrorHandler, Map<String, String> uriToUploadDirectorMap) {
        Objects.requireNonNull(vertx);
        Objects.requireNonNull(requestProcessingErrorHandler);
        Objects.requireNonNull(uriToUploadDirectorMap);
        this.vertx = vertx;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
        this.uriToUploadDirectorMap = uriToUploadDirectorMap;
    }

    @Override
    public void handle(RoutingContext ctx) {

        final String uploadDir = uriToUploadDirectorMap.get(ctx.request().uri());

        createDirsIfNotExists(uploadDir);

        Promises
            .when(
                ctx.fileUploads().stream()
                    .map(fileUpload -> uploadNonBlocking(fileUpload.uploadedFileName(), uploadDir + "/" + fileUpload.fileName()))
                    .collect(Collectors.toList())
            )
            .then(strings -> ctx.response().end(new JsonArray(strings).toString()))
            .err(throwable -> requestProcessingErrorHandler.handleError(throwable, ctx))
        ;
    }

    private Promise<String> uploadNonBlocking(String from, String to) {
        final Defer<String> defer = Promises.defer();

        vertx.fileSystem()
            .move(from, to, makeDeferred(defer, to));

        return defer.promise();
    }

    private Handler<AsyncResult<Void>> makeDeferred(Defer<String> defer, String to) {

        return event -> {

            if (event.failed()) {
                defer.reject(event.cause());
                return;
            }

            defer.resolve(to);
        };
    }

    private void createDirsIfNotExists(String uploadDir) {
        File file = new File(uploadDir);
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }
}
