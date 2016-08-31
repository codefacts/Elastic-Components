package elasta.composer.endpoints;

import elasta.composer.util.ExceptionUtil;
import elasta.composer.util.Util;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;

/**
 * Created by shahadat on 3/3/16.
 */
public class FileWriteEndPoint implements Endpoint<Buffer> {
    private static final String PATH = "path";
    private final FileSystem fileSystem;

    public FileWriteEndPoint(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void process(Message<Buffer> message) {
        Defer<Void> defer = Promises.<Void>defer();

        fileSystem.writeFile(message.headers().get(PATH), message.body(), Util.makeDeferred(defer));

        defer.promise()
            .then(v -> message.reply(null))
            .error(e -> ExceptionUtil.fail(message, e));
    }
}
