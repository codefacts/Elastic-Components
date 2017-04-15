package elasta.orm.upsert;

import lombok.Value;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
@Value
final public class BelongsTo {
    final String field;
    final BelongToHandler belongToHandler;
    final DependencyColumnValuePopulator dependencyColumnValuePopulator;

    public BelongsTo(String field, BelongToHandler belongToHandler, DependencyColumnValuePopulator dependencyColumnValuePopulator) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(belongToHandler);
        Objects.requireNonNull(dependencyColumnValuePopulator);
        this.field = field;
        this.belongToHandler = belongToHandler;
        this.dependencyColumnValuePopulator = dependencyColumnValuePopulator;
    }
}
