package br.net.mirante.singular.util.wicket.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;

import br.net.mirante.singular.util.wicket.lambda.ILambdasMixin;

public final class WicketUtils {

    public static final IBehaviorsMixin  $b = new IBehaviorsMixin() {};
    public static final IModelsMixin     $m = new IModelsMixin() {};
    public static final IValidatorsMixin $v = new IValidatorsMixin() {};
    public static final ILambdasMixin    $L = new ILambdasMixin() {};

    private WicketUtils() {}

    @SuppressWarnings("unchecked")
    public static <C> Stream<C> findChildren(MarkupContainer container, Class<C> type) {
        Stream.Builder<C> builder = Stream.builder();
        container.visitChildren(type, (Component object, IVisit<Void> visit) -> {
            if (type.isAssignableFrom(object.getClass())) {
                builder.add((C) object);
            }
        });
        return builder.build();
    }

    public static <C> Optional<C> findFirstChild(MarkupContainer container, Class<C> type) {
        return findFirstChild(container, type, null);
    }

    @SuppressWarnings("unchecked")
    public static <C> Optional<C> findFirstChild(MarkupContainer container, Class<C> type, Predicate<C> filter) {
        return Optional.ofNullable(container.visitChildren(type, (Component object, IVisit<C> visit) -> {
            if (type.isAssignableFrom(object.getClass()) && (filter == null || filter.test((C) object))) {
                visit.stop((C) object);
            }
        }));
    }

    public static void clearMessagesForComponent(Component component) {
        new FeedbackCollector(component.getPage())
            .collect(message -> Objects.equals(message.getReporter(), component)).stream()
            .forEach(it -> it.markRendered());
    }

    public static List<MarkupContainer> listParents(Component reporter) {
        List<MarkupContainer> list = new ArrayList<MarkupContainer>();
        if (reporter != null) {
            MarkupContainer container = reporter.getParent();
            while (container != null) {
                list.add(container);
                container = container.getParent();
            }
        }
        return list;
    }

    public static boolean nullOrEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj instanceof IModel<?>)
            return nullOrEmpty(((IModel<?>) obj).getObject());
        if (obj instanceof String)
            return ((String) obj).trim().isEmpty();
        if (obj instanceof Collection<?>)
            return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map<?, ?>)
            return ((Map<?, ?>) obj).isEmpty();
        if (obj instanceof Iterator<?>)
            return ((Iterator<?>) obj).hasNext();
        return true;
    }

    public static Optional<String> findPageRelativePath(MarkupContainer container, String childId) {
        return WicketUtils.findFirstChild(container, Component.class,
            it -> it.getId().equals(childId)).map(it -> it.getPageRelativePath());
    }
    public static Optional<String> findContainerRelativePath(MarkupContainer container, String childId) {
        return WicketUtils.findFirstChild(container, Component.class, it -> it.getId().equals(childId))
            .map(it -> it.getPageRelativePath())
            .map(it -> it.substring(container.getPageRelativePath().length() + 1));
    }
}
