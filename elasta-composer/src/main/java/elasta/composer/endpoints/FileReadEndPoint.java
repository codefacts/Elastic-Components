package elasta.composer.endpoints;

import io.crm.promise.Promises;
import io.crm.promise.intfs.Defer;
import io.crm.util.ExceptionUtil;
import io.crm.util.Util;
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
            .error(e -> ExceptionUtil.fail(message, e));
    }
}
