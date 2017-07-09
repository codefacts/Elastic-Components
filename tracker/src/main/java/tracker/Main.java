package tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by sohan on 6/22/2017.
 */
public class Main {
    public static void main(String[] asdf) {

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        System.out.println(nowAsISO);
        System.out.println(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
    }
}
