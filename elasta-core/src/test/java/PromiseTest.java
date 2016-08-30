import elasta.promise.impl.Promises;
import org.junit.Test;

/**
 * Created by Jango on 8/31/2016.
 */
public class PromiseTest {

    @Test
    public void test1() {
        Promises.empty().complete(v -> System.out.println());
    }
}
