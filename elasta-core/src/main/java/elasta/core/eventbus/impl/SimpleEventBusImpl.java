package elasta.core.eventbus.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.eventbus.*;
import elasta.core.eventbus.EventListener;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.core.touple.MutableTpl1;
import elasta.core.utils.PathTemplate;
import elasta.core.utils.PathTemplateImpl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Jango on 11/5/2016.
 */
public class SimpleEventBusImpl implements SimpleEventBus {
    private final char PATH_SEPERATOR = '.';
    private Map<String, List<EventMatchInfo>> handlersMap = new ConcurrentHashMap<>();
    private List<EventHandlerInfo> handlerInfos = new CopyOnWriteArrayList<>();

    @Override
    public <T, R> SimpleEventBus addProcessor(String event, Processor<T, R> processor) {
        this.<T, R>addListener(event, (o, context) -> context.next(
            processor.handle(o, context.event(), context.params())
        ));
        return this;
    }

    @Override
    public <T, R> SimpleEventBus addProcessorP(String event, ProcessorP<T, R> processorP) {
        this.<T, R>addListener(event, (o, context) -> processorP.handle(o, context.event(), context.params())
            .mapP(context::<R>next));
        return this;
    }

    @Override
    public <T> SimpleEventBus addFilter(String event, Filter<T> filter) {

        this.<T, Object>addListener(event, (o, context) -> {
            boolean isNext = filter.handle(o, context.event(), context.params());
            if (isNext) {
                return context.next(o);
            } else {
                return Promises.of(o);
            }
        });

        return this;
    }

    @Override
    public <T> SimpleEventBus addFilterP(String event, FilterP<T> filterP) {
        this.<T, Object>addListener(event, (value, context) -> filterP.handle(value, context.event(), context.params())
            .mapP(isNext -> {

                if (isNext != null && isNext) {
                    return context.next(value);
                }

                return Promises.<Object>of(value);
            }));
        return this;
    }

    @Override
    public <T> SimpleEventBus addHandler(String event, EventHandler<T> handler) {
        this.<T, Object>addListener(event, (o, context) -> {
            handler.handle(o, context.event(), context.params());
            return context.next(o);
        });
        return this;
    }

    @Override
    public <T> SimpleEventBus addHandlerP(String event, EventHandlerP<T> handlerP) {
        this.<T, Object>addListener(event, (o, context) -> handlerP.handle(o, context.event(), context.params()).mapP(aVoid -> context.next(o)));
        return this;
    }

    @Override
    public <T, R> SimpleEventBus addListener(String eventPattern, EventListener<T, R> eventListener) {
        handlerInfos.add(new EventHandlerInfo(eventPattern, eventListener));

        handlersMap.keySet().forEach(evet -> {
            PathTemplate.MatchAndParams matchAndParams = new PathTemplateImpl(eventPattern).matchAndParams(evet, PATH_SEPERATOR);
            if (matchAndParams.isMatch()) {

                handlersMap.put(evet,
                    ImmutableList.<EventMatchInfo>builder()
                        .add(new EventMatchInfo(eventListener, matchAndParams.getParams()))
                        .addAll(handlersMap.get(evet)).build()
                );
            }
        });

        return this;
    }

    @Override
    public <R> Promise<R> fire(String event, Object t) {
        return fire(event, t, ImmutableMap.of());
    }

    @Override
    public <R> Promise<R> fire(String event, Object t, Map<String, ?> extra) {
        List<EventMatchInfo> handlers = handlersMap.get(event);

        if (handlers == null) {

            handlers = findHandlers(event);

            handlersMap.put(event, handlers);

        }

        return executeHandlers(handlers, event, t, extra);
    }

    private List<EventMatchInfo> findHandlers(String event) {

        ImmutableList.Builder<EventMatchInfo> listBuilder = ImmutableList.builder();

        handlerInfos.forEach(eventHandlerInfo -> {

            PathTemplate.MatchAndParams match = match(eventHandlerInfo.eventPattern, event);

            if (match.isMatch()) {
                listBuilder.add(new EventMatchInfo(eventHandlerInfo.eventListener, match.getParams()));
            }

        });

        return listBuilder.build();

    }

    private PathTemplate.MatchAndParams match(String eventPattern, String event) {
        return new PathTemplateImpl(eventPattern).matchAndParams(event, PATH_SEPERATOR);
    }

    private <T, R> Promise<R> executeHandlers(List<EventMatchInfo> handlers, String event, T t, Map<String, ?> extra) {

        if (handlers.isEmpty()) {
            return Promises.of((R) t);
        }

        Iterator<EventMatchInfo> iterator = handlers.iterator();
        EventMatchInfo info = iterator.next();

        Map<String, ?> map = new HashMap<>(info.params);
        map.putAll((Map) extra);

        MutableTpl1<ContextImpl> tpl1 = new MutableTpl1<>();
        tpl1.t1 = new ContextImpl(event, map, val -> {
            if (!iterator.hasNext()) {
                return Promises.of(val);
            }
            EventMatchInfo matchInfo = iterator.next();
            tpl1.t1.params().putAll(matchInfo.params);
            return matchInfo.eventListener.handle(val, tpl1.t1);
        });

        return info.eventListener.handle(t, tpl1.t1);
    }

    private class EventHandlerInfo {
        final String eventPattern;
        final EventListener eventListener;

        private EventHandlerInfo(String eventPattern, EventListener eventListener) {
            this.eventPattern = eventPattern;
            this.eventListener = eventListener;
        }
    }

    public static void main(String[] args) {

//        System.out.println(new SimpleEventBusImpl().match("ss.{11}.{22}.{33}.*", "ss.{i}.{j}.{k}.sohan.kona.jama"));
//        System.out.println("kk".substring("kk".indexOf("kk") + "kk".length()));
//
//     System.out.println("{so}".substring(1, "{}".length() - 1));
//        System.out.println("LL.KK.MM.ZZ".substring("LL.KK.MM.ZZ".indexOf("KK") + "KK".length()));
//        Matcher jj = Pattern.compile("jj").matcher("kk.jj.mm.nn");
//        jj.find();
//        System.out.println("kk.jj.mm.nn".substring(jj.start()));

        SimpleEventBusImpl eventbus = new SimpleEventBusImpl();
        SimpleEventBus listener = eventbus.<String, Object>addListener("book.create", (o, context) -> {
            System.out.println("ok: " + o + " " + context);
            return context.next(o);
        });

        eventbus.addListener("book.{op}", (o, context) -> {
            context.params().put("ha", 444);
            System.out.println("ok2: " + o + " " + context);
            return context.next(o);
        });

        eventbus.addListener("*", (o, context) -> {
            context.params().put("tu", 555);
            System.out.println("ok3: " + o + " " + context);
            return context.next(o);
        });

        eventbus.addListener("{entity}.{op}", (o, context) -> {
            context.params().put("ll", 8655);
            System.out.println("ok4: " + o + " " + context);
            return context.next(o);
        });

        eventbus.fire("book.create", "go it", Collections.singletonMap("me", "sohan"));
        eventbus.fire("book.create", "go it");
        eventbus.fire("book.create", "go it");
    }

    private static class EventMatchInfo {
        final EventListener eventListener;
        final Map<String, String> params;

        private EventMatchInfo(EventListener eventListener, Map<String, String> params) {
            this.eventListener = eventListener;
            this.params = params;
        }
    }

}
