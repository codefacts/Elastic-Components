package elasta.composer;

import java.time.Instant;
import java.util.Date;

/**
 * Created by sohan on 7/8/2017.
 */
public interface AppDateTimeFormatter {

    Instant parseInstant(String obj);

    Date parseDate(String obj);

    String format(Instant instant);

    String format(Date date);
}
