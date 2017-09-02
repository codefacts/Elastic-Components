import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by sohan on 2017-08-06.
 */
public interface ParseCsv {

    static void main(String[] args) throws Exception {

        PublishSubject<Map<String, String>> subject = PublishSubject.create();

        compose(subject);

        parseFile(
            "C:\\Users\\sohan\\Desktop\\SND 216-17 Outlet list.csv",
            subject
        );

        Thread.sleep(1000000);
    }

    static void compose(PublishSubject<Map<String, String>> subject) {

        final long start = System.currentTimeMillis();

        List<Single<? extends List<? extends Serializable>>> singles = Arrays.asList(

//            doRootPrint(subject, MaricoOutletModel.region),
//
//            doPrint(subject, ImmutableList.of(MaricoOutletModel.region), MaricoOutletModel.area),
//
//            doPrint(subject, ImmutableList.of(MaricoOutletModel.region, MaricoOutletModel.area), MaricoOutletModel.town),
//
//            doPrint(subject, ImmutableList.of(MaricoOutletModel.region, MaricoOutletModel.area, MaricoOutletModel.town), MaricoOutletModel.district),

//            doPrint(
//                subject, ImmutableList.of(
//                    MaricoOutletModel.area, MaricoOutletModel.town, MaricoOutletModel.district
//                ),
//                MaricoOutletModel.thana
//            ),
//
//            doPrint(subject,
//                ImmutableList.of(
//                    MaricoOutletModel.area, MaricoOutletModel.town, MaricoOutletModel.district, MaricoOutletModel.thana
//                ),
//                MaricoOutletModel._union
//            ),
//            doPrint(subject,
//                ImmutableList.of(
//                    MaricoOutletModel.area, MaricoOutletModel.town, MaricoOutletModel.district, MaricoOutletModel.thana,
//                    MaricoOutletModel._union
//                ),
//                MaricoOutletModel.village
//            ),
//
            doRootPrint(subject, MaricoOutletModel.midus),

            doRootPrint(subject, MaricoOutletModel.dd_sd),

            doRootPrint(subject, MaricoOutletModel.system_ol),
            doRootPrint(subject, MaricoOutletModel.ol_status),

            doRootPrint(subject, MaricoOutletModel.channel),
            doRootPrint(subject, MaricoOutletModel._type)

//            doRootPrint(subject, MaricoOutletModel.tm_name),
//            doRootPrint(subject, MaricoOutletModel.distributor_code),
//            doRootPrint(subject, MaricoOutletModel.distributor_name),

//            doRootPrint(subject, MaricoOutletModel.psr_name),
//            doRootPrint(subject, MaricoOutletModel.beat_name),
//            doRootPrint(subject, MaricoOutletModel.stockist_name),
//
//            doRootPrint(subject, MaricoOutletModel.retailer_code),
//            doRootPrint(subject, MaricoOutletModel.retailer_name),
//            doRootPrint(subject, MaricoOutletModel.address),

//            doRootPrint(subject, MaricoOutletModel.mobile_no)
        );

        Single
            .zip(
                singles, objects -> {
//                    System.out.println(Arrays.toString(objects));
                    return objects;
                }
            )
            .subscribe(objects -> {
                long end = System.currentTimeMillis();
                System.out.println("time: " + (end - start) / 1000 + " seconds");
                System.out.println("active threads: " + Thread.activeCount());
            }, Throwable::printStackTrace)
        ;
    }

    static Single<List<String>> doRootPrint(PublishSubject<Map<String, String>> subject, String key) {
        return subject
            .observeOn(Schedulers.single())
            .skip(1)
            .map(map -> map.get(key))
            .distinct()
            .toSortedList()
//            .doOnSuccess(simpleImmutableEntries -> System.out.println(Thread.currentThread()))
            .doOnSuccess(strings -> {
                strings.add(0, "Select Item");
                System.out.println("\"" + key + "\": " + new JsonArray(strings).encode() + ",");
            })
            .doOnError(Throwable::printStackTrace)
            ;
    }

    static Single<List<AbstractMap.SimpleImmutableEntry<String, List<String>>>> doPrint(PublishSubject<Map<String, String>> subject, List<String> keys, String key) {
        return subject
            .observeOn(Schedulers.single())
            .skip(1)
            .groupBy(map -> toGroupKey(map, keys), map -> map.get(key))
            .flatMap(
                goups -> goups.distinct()
                    .toSortedList().toObservable()
                    .map(strings -> new AbstractMap.SimpleImmutableEntry<>(goups.getKey(), strings))
            )
            .sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()))
            .toList()
//            .doOnSuccess(simpleImmutableEntries -> System.out.println(Thread.currentThread()))
            .subscribeOn(Schedulers.computation())
            .doOnSuccess(entries -> {

                entries.forEach(entry -> {
                    entry.getValue().add(0, "Select Item");
                    System.out.println("\"" + entry.getKey() + "\"" + ":" + new JsonArray(entry.getValue()).encode() + ",");
                });

            })
            .doOnError(Throwable::printStackTrace);
    }

    static String toGroupKey(Map<String, String> map, List<String> keys) {
        return keys.stream().map(key -> map.get(key)).collect(Collectors.joining("|"));
    }

    static void parseFile(final String fileStr, final PublishSubject<Map<String, String>> subject) throws Exception {

        final CSVParser parser = CSVParser.parse(
            new File(fileStr), StandardCharsets.UTF_8, CSVFormat.DEFAULT
        );

        try {
            parser.forEach(csvRecord -> subject.onNext(toMap(csvRecord)));
            subject.onComplete();
        } finally {
            parser.close();
        }
    }

    static Map<String, String> toMap(CSVRecord csvRecord) {
        List<String> list = StreamSupport.stream(csvRecord.spliterator(), false).collect(Collectors.toList());

//        if (list.size() != headers.length) {
//            if (headers.length < list.size()) {
//                printRows("headers", "list", Arrays.asList(headers), list);
//            } else {
//                printRows("list", "headers", list, Arrays.asList(headers));
//            }
//            throw new RuntimeException("Inconsistent csv record " + list.size() + " != " + headers.length);
//        }

        final ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();

        for (int i = 0, len = Math.min(headers.length, list.size()); i < len; i++) {
            mapBuilder.put(headers[i], list.get(i).trim());
        }

        return mapBuilder.build();
    }

    static void printRows(String key11, String key22, List<String> list11, List<String> list22) {
        System.out.println("##### headers: " + key11 + " : " + key22);
        for (int i = 0; i < list11.size(); i++) {
            System.out.println("### " + list11.get(i) + " => " + list22.get(i));
        }
    }

    String[] headers = {
        MaricoOutletModel.serial_no,
//        MaricoOutletModel.region,
        MaricoOutletModel.area,
        MaricoOutletModel.town,
        MaricoOutletModel.district,

        MaricoOutletModel.thana,
        MaricoOutletModel._union,
        MaricoOutletModel.village,
        MaricoOutletModel.tm_name,

        MaricoOutletModel.distributor_code,
        MaricoOutletModel.distributor_name,
        MaricoOutletModel.midus,
        MaricoOutletModel.dd_sd,

        MaricoOutletModel.psr_name,
        MaricoOutletModel.beat_name,
        MaricoOutletModel.stockist_name,
        MaricoOutletModel.retailer_code,

        MaricoOutletModel.retailer_name,
        MaricoOutletModel.address,
        MaricoOutletModel.mobile_no,
        MaricoOutletModel.system_ol,

        MaricoOutletModel.ol_status,
        MaricoOutletModel.pcno,
        MaricoOutletModel.beli,
        MaricoOutletModel.cooling,

        MaricoOutletModel.nihar,
        MaricoOutletModel.extra_care,
        MaricoOutletModel.hair_code,
        MaricoOutletModel.kk,

        MaricoOutletModel.setwet,
        MaricoOutletModel.cute,
        MaricoOutletModel.dabur_amla,
        MaricoOutletModel.vatika,

        MaricoOutletModel.navaratna,
        MaricoOutletModel.lux,
        MaricoOutletModel.kumarika,
        MaricoOutletModel.meril_lotion,

        MaricoOutletModel.vaseline_lotion,
        MaricoOutletModel.pbl,
        MaricoOutletModel.godrej_dye,
        MaricoOutletModel.vasmol_liquid,

        MaricoOutletModel.any_other_deo,
        MaricoOutletModel.english_anty_lice_shampo,
        MaricoOutletModel.channel,
        MaricoOutletModel._type,

        MaricoOutletModel.avg_bpm,
        MaricoOutletModel.avg_mpm
    };
}
