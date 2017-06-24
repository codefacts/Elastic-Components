package elasta.orm;

import elasta.core.promise.impl.Promises;

/**
 * Created by sohan on 3/17/2017.
 */
public class Main {
    public static void main(String[] asfd) {

        Promises.empty().filter(o -> true).then(o -> System.out.println("passed"));
    }
}
