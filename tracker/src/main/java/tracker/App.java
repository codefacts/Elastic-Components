package tracker;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/25/2017.
 */
public interface App {

    App start(Config config);

    App stop();

    @Value
    @Builder
    final class Config {
        final DB db;

        public Config(DB db) {
            Objects.requireNonNull(db);
            this.db = db;
        }
    }

    @Value
    @Builder
    final class DB {
        final String user;
        final String password;
        final String database;
        final String host;
        final String port;
        final String driverClassName;

        public DB(String user, String password, String database, String host, String port, String driverClassName) {
            Objects.requireNonNull(user);
            Objects.requireNonNull(password);
            Objects.requireNonNull(database);
            Objects.requireNonNull(host);
            Objects.requireNonNull(port);
            Objects.requireNonNull(driverClassName);
            this.user = user;
            this.password = password;
            this.database = database;
            this.host = host;
            this.port = port;
            this.driverClassName = driverClassName;
        }
    }
}
