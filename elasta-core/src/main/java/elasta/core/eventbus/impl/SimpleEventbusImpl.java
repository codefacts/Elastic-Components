package elasta.core.eventbus.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.eventbus.Handler;
import elasta.core.eventbus.SimpleEventbus;
import elasta.core.promise.intfs.Promise;
import elasta.core.utils.PathTemplate;
import elasta.core.utils.PathTemplateImpl;

import java.util.*;

/**
 * Created by Jango on 11/5/2016.
 */
public class SimpleEventbusImpl implements SimpleEventbus {
    private final char PATH_SEPERATOR = '.';
    private Map<String, List<EventMatchInfo>> handlersMap = new HashMap<>();
    private List<EventHandlerInfo> handlerInfos = new ArrayList<>();

    @Override
    public <T, R> SimpleEventbus addListener(String eventPattern, Handler<T, R> handler) {
        handlerInfos.add(new EventHandlerInfo(eventPattern, handler));
        return this;
    }

    @Override
    public <T, R> Promise<R> fire(String event, T t) {

        List<EventMatchInfo> handlers = handlersMap.get(event);

        if (handlers == null || handlers.isEmpty()) {

            handlers = findHandlers(event);

            handlersMap.put(event, handlers);

        }

        return executeHandlers(handlers, event, t);
    }

    private List<EventMatchInfo> findHandlers(String event) {

        ImmutableList.Builder<EventMatchInfo> listBuilder = ImmutableList.builder();

        handlerInfos.forEach(eventHandlerInfo -> {

            PathTemplate.MatchAndParams match = match(eventHandlerInfo.eventPattern, event);

            if (match.isMatch()) {
                listBuilder.add(new EventMatchInfo(eventHandlerInfo.handler, match.getParams()));
            }

        });

        return listBuilder.build();

    }

    private PathTemplate.MatchAndParams match(String eventPattern, String event) {
        return new PathTemplateImpl(eventPattern).matchAndParams(event, PATH_SEPERATOR);
    }

//    private MatchAndParams _doMatchStuff(String eventPattern, String event) {
//        Pattern pattern = Pattern.compile("\\{[^\\{]*?\\}");
//
//        Matcher matcher = pattern.matcher(eventPattern);
//
//        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();
//
//        int e1;
//        String key1;
//
//        if (!matcher.find()) {
//            return new MatchAndParams(eventPattern.equals(event), ImmutableMap.of());
//        }
//
//        e1 = matcher.end();
//        key1 = matcher.group();
//        key1 = key1.substring(1, key1.length() - 1);
//
//        int start = matcher.start();
//
//        if (!event.startsWith(eventPattern.substring(0, start))) {
//
//            return new MatchAndParams(false, ImmutableMap.of());
//        }
//
//        event = event.substring(start);
//
//        for (; matcher.find(); ) {
//
//            int s2 = matcher.start(), e2 = matcher.end();
//            String key2 = matcher.group();
//
//            String part = eventPattern.substring(e1, s2);
//            int indexOf = event.indexOf(part);
//            if (indexOf < 0) {
//                return new MatchAndParams(false, ImmutableMap.of());
//            }
//
//            if (!key1.isEmpty()) {
//
//                String key1Val = event.substring(0, indexOf);
//                mapBuilder.put(key1, key1Val);
//            }
//
//            e1 = e2;
//            key1 = key2.substring(1, key2.length() - 1);
//
//            event = event.substring(indexOf + part.length());
//        }
//
//        String sub = eventPattern.substring(e1);
//        if (sub.isEmpty()) {
//            if (!key1.isEmpty()) {
//                mapBuilder.put(key1, event);
//            }
//            return new MatchAndParams(true, mapBuilder.build());
//        }
//
//        int indexOf = event.indexOf(sub);
//        if (indexOf < 0) {
//            return new MatchAndParams(false, ImmutableMap.of());
//        }
//
//        if (!key1.isEmpty()) {
//
//            String key1Val = event.substring(0, indexOf);
//            mapBuilder.put(key1, key1Val);
//        }
//
//        return new MatchAndParams(true, mapBuilder.build());
//    }

    private boolean charEquals(String eventPattern, int start, int end, String event, int s2) {

        for (int i = start; i < end; i++) {
            if (!(eventPattern.charAt(i) == event.charAt(s2++))) {
                return false;
            }
        }

        return true;
    }

    private <T, R> Promise<R> executeHandlers(List<EventMatchInfo> handlers, String event, T t) {

        return null;
    }

    private class EventHandlerInfo {
        final String eventPattern;
        final Handler handler;

        private EventHandlerInfo(String eventPattern, Handler handler) {
            this.eventPattern = eventPattern;
            this.handler = handler;
        }
    }

    public static void main(String[] args) {

        System.out.println(new SimpleEventbusImpl().match("ss.{11}.{22}.{33}.*", "ss.{i}.{j}.{k}.sohan.kona.jama"));
//        System.out.println("kk".substring("kk".indexOf("kk") + "kk".length()));
//
//     System.out.println("{so}".substring(1, "{}".length() - 1));
//        System.out.println("LL.KK.MM.ZZ".substring("LL.KK.MM.ZZ".indexOf("KK") + "KK".length()));
//        Matcher jj = Pattern.compile("jj").matcher("kk.jj.mm.nn");
//        jj.find();
//        System.out.println("kk.jj.mm.nn".substring(jj.start()));
    }

    private class MTpl {
        final int start;
        final int end;
        final String key;

        private MTpl(int start, int end, String key) {
            this.start = start;
            this.end = end;
            this.key = key;
        }
    }

    private static class EventMatchInfo {
        final Handler handler;
        final Map<String, String> params;

        private EventMatchInfo(Handler handler, Map<String, String> params) {
            this.handler = handler;
            this.params = params;
        }
    }
}
