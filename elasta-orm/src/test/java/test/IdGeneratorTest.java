package test;

import elasta.orm.idgenerator.impl.LongIdGeneratorImpl;

import java.util.HashSet;

/**
 * Created by sohan on 6/27/2017.
 */
public interface IdGeneratorTest {
    static void main(String[] asdf) {
        LongIdGeneratorImpl longIdGenerator = new LongIdGeneratorImpl();

        int count = 100000000;

        HashSet<Long> objects = new HashSet<>(count);

        long start = System.nanoTime();

        for (int i = 0; i < count; i++) {

            Long val = longIdGenerator.nextId("").val();

//            long val = UUID.randomUUID().getLeastSignificantBits() >>> 1;
            System.out.println(i + " : " + val);

            boolean add = objects.add(val);
            if (!add) {
                throw new RuntimeException(i + ": ID '" + val + "' exists");
            }
        }

        long end = System.nanoTime();

        System.out.println("end: " + end + " start: " + start);
        System.out.println("end - start: " + (end - start));
        System.out.println("time: " + (end - start) / 1E6 + " ms");
    }
}
