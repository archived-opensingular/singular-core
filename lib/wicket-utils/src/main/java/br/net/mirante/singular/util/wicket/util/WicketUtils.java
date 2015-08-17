package br.net.mirante.singular.util.wicket.util;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class WicketUtils {

    private WicketUtils() {
    }

    public static <C> Stream<C> findChildren(MarkupContainer container, Class<C> type) {
        Stream.Builder<C> builder = Stream.builder();
        container.visitChildren(type, new IVisitor<Component, Void>() {
            @Override
            @SuppressWarnings("unchecked")
            public void component(Component object, IVisit<Void> visit) {
                if (type.isAssignableFrom(object.getClass()))
                    builder.add((C) object);
            }
        });
        return builder.build();
    }

    public static <C> Optional<C> findFirstChild(MarkupContainer container, Class<C> type) {
        return findFirstChild(container, type, null);
    }

    public static <C> Optional<C> findFirstChild(MarkupContainer container, Class<C> type, Predicate<C> filter) {
        return Optional.ofNullable(container.visitChildren(type, new IVisitor<Component, C>() {
            @Override
            @SuppressWarnings("unchecked")
            public void component(Component object, IVisit<C> visit) {
                if (type.isAssignableFrom(object.getClass()) && (filter == null || filter.test((C) object))) {
                    visit.stop((C) object);
                }
            }
        }));
    }

    public static void clearMessagesForComponent(Component component) {
        new FeedbackCollector(component.getPage())
            .collect(message -> message.getReporter() == component).stream()
            .forEach(it -> it.markRendered());
    }

    public static List<MarkupContainer> listParents(Component reporter) {
        List<MarkupContainer> list = new ArrayList<MarkupContainer>();
        if(reporter != null) {
            MarkupContainer container = reporter.getParent();
            while(container != null) {
                list.add(container);
                container = container.getParent();
            }
        }
        return list;
        
    }
}
