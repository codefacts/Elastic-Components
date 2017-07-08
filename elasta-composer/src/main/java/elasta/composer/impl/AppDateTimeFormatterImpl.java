package elasta.composer.impl;

import elasta.composer.AppDateTimeFormatter;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

/**
 * Created by sohan on 7/8/2017.
 */
final public class AppDateTimeFormatterImpl implements AppDateTimeFormatter {
    @Override
    public Instant parseInstant(String dateStr) {

        Objects.requireNonNull(dateStr);

        return Instant.from(
            ISO_INSTANT.parse(dateStr)
        );
    }

    @Override
    public Date parseDate(String dateStr) {

        return Date.from(
            parseInstant(dateStr)
        );
    }

    @Override
    public String format(Instant instant) {
        Objects.requireNonNull(instant);
        return ISO_INSTANT.format(instant);
    }

    @Override
    public String format(Date date) {
        Objects.requireNonNull(date);
        return ISO_INSTANT.format(date.toInstant());
    }
}
