package elasta.composer.interceptor.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.AppDateTimeFormatter;
import elasta.composer.Msg;
import elasta.composer.interceptor.DbOperationInterceptor;
import elasta.sql.core.UpdateTpl;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 7/8/2017.
 */
final public class DbOperationInterceptorImpl implements DbOperationInterceptor {
    final AppDateTimeFormatter appDateTimeFormatter;
    final String createdByColumn;
    final String updatedByColumn;
    final String createDateColumn;
    final String updateDateColumn;

    public DbOperationInterceptorImpl(AppDateTimeFormatter appDateTimeFormatter, String createdByColumn, String updatedByColumn, String createDateColumn, String updateDateColumn) {
        Objects.requireNonNull(appDateTimeFormatter);
        Objects.requireNonNull(createdByColumn);
        Objects.requireNonNull(createDateColumn);
        Objects.requireNonNull(updatedByColumn);
        Objects.requireNonNull(updateDateColumn);
        this.appDateTimeFormatter = appDateTimeFormatter;
        this.createdByColumn = createdByColumn;
        this.updatedByColumn = updatedByColumn;
        this.createDateColumn = createDateColumn;
        this.updateDateColumn = updateDateColumn;
    }

    @Override
    public <T> UpdateTpl intercept(UpdateTpl updateTpl, Msg<T> msg) {

        switch (updateTpl.getUpdateOperationType()) {

            case INSERT: {
                return new UpdateTpl(
                    updateTpl.getUpdateOperationType(),
                    updateTpl.getTable(),
                    with(
                        updateTpl.getData().getMap(),
                        ImmutableMap.of(
                            createdByColumn, msg.userId(),
                            createDateColumn, now()
                        )
                    ),
                    updateTpl.getSqlConditions()
                );
            }
            case UPDATE: {
                return new UpdateTpl(
                    updateTpl.getUpdateOperationType(),
                    updateTpl.getTable(),
                    with(updateTpl.getData().getMap(), ImmutableMap.of(
                        updatedByColumn, msg.userId(),
                        updateDateColumn, now()
                    )),
                    updateTpl.getSqlConditions()
                );
            }
        }

        return updateTpl;
    }

    private String now() {
        return appDateTimeFormatter.format(Instant.now());
    }

    private JsonObject with(Map<String, Object> map1, Map<String, Object> map2) {

        HashMap<String, Object> map = new HashMap<>(map1);

        map.putAll(map2);

        return new JsonObject(
            ImmutableMap.copyOf(map)
        );
    }
}
