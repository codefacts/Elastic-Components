package elasta.composer.endpoints;

import elasta.composer.util.ExceptionUtil;
import elasta.composer.util.Util;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;

/**
 * Created by shahadat on 3/4/16.
 */
public class FileReadEndPoint implements Endpoint<String> {
    private final FileSystem fileSystem;

    public FileReadEndPoint(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void process(Message<String> message) {
        Defer<Buffer> defer = Promises.<Buffer>defer();

        fileSystem.readFile(message.body(), Util.makeDeferred(defer));

        defer.promise()
            .then(buffer -> message.reply(buffer))
            .err(e -> ExceptionUtil.fail(message, e));
    }
}
