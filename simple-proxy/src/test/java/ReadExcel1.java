import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by sohan on 2017-08-28.
 */
public interface ReadExcel1 {
    static void main(String[] args) throws Exception {

        PublishSubject<Map<String, String>> subject1 = PublishSubject.create();
        PublishSubject<Map<String, String>> subject2 = PublishSubject.create();

        Observable.timer(500, TimeUnit.MILLISECONDS)
            .doOnNext(aLong -> {
                ParseCsv.parseFile("C:\\Users\\sohan\\Desktop\\Outlet list Format.csv", subject1);
                ParseCsv.parseFile("C:\\Users\\sohan\\Desktop\\SND 216-17 Outlet list.csv", subject2);
            })
            .subscribe();

        List<String> list1 = subject1
            .map(stringStringMap -> stringStringMap.get(MaricoOutletModel.area))
            .map(s -> s.toUpperCase())
            .distinct()
            .toSortedList()
            .subscribeOn(Schedulers.io())
            .blockingGet();
//            .subscribe(stringStringMap -> {
//                System.out.println(stringStringMap.stream().collect(Collectors.joining("\n")) + "\n\n");
//            });

        List<String> list2 = subject2
            .map(stringStringMap -> stringStringMap.get(MaricoOutletModel.region))
            .map(s -> s.toUpperCase())
            .distinct()
            .toSortedList()
            .subscribeOn(Schedulers.io())
            .blockingGet();
//            .subscribe(stringStringMap -> {
//                System.out.println(stringStringMap.stream().collect(Collectors.joining("\n")));
//            });

        list1.removeAll(list2);
        System.out.println(list1.stream().collect(Collectors.joining("\n")) + "\n\n");

        System.out.println("list1:\n " + list1.stream().collect(Collectors.joining("\n")));
        System.out.println("\n\n");
        System.out.println("list2:\n " + list2.stream().collect(Collectors.joining("\n")));
    }
}
