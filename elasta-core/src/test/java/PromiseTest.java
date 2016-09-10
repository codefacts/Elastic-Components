import elasta.core.promise.impl.Promises;
import org.junit.Test;

/**
 * Created by Jango on 8/31/2016.
 */
public class PromiseTest {

    @Test
    public void test1() {
        Promises.empty().cmp(v -> System.out.println());
    }
}
