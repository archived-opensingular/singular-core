package br.net.mirante.singular.form.wicket.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import com.google.common.collect.Lists;

public class TestFinders {

    public static Optional<String> findId(MarkupContainer container, String leafName) {
        Iterator<Component> it = container.iterator();
        while (it.hasNext()) {
            Optional<String> found = findInComponent(leafName, it.next());
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }   

    private static Optional<String> findInComponent(String leafName, Component c) {
        if (c.getId().equals(leafName)) {
            return Optional.of(leafName);
        } else if (c instanceof MarkupContainer) {
            Optional<String> found = findId((MarkupContainer) c, leafName);
            if (found.isPresent()) {
                return Optional.of(c.getId() + ":" + found.get());
            }
        }
        return Optional.empty();
    }

    public static List<Component> findTag(MarkupContainer container, String id, Class<? extends Component> tag) {
        List<Component> tags = findTag(container, tag);
        return tags.stream().filter((c) -> c.getId().equals(id)).collect(Collectors.toList());
    }

    public static List<Component> findTag(MarkupContainer container, Class<? extends Component> tag) {
        List<Component> result = Lists.newArrayList();
        findTag(tag, result, container);
        return result;
    }
    
    private static void findTag(Class<? extends Component> tag, List<Component> result, MarkupContainer c) {
        Iterator<Component> it = c.iterator();
        while (it.hasNext()) {
            findInComponent(tag, result, it.next());
        }
    }
    
    private static void findInComponent(Class<? extends Component> tag, List<Component> result, Component c) {
        if (tag.isInstance(c)) {
            result.add(c);
        } else if (c instanceof MarkupContainer) {
            findTag(tag, result, (MarkupContainer) c);
        }
    }

    public static Component findFirstComponentWithId(MarkupContainer container, String id) {
        return container.visitChildren(Component.class, new IVisitor<Component, Component>() {
            @Override
            public void component(Component object, IVisit<Component> visit) {
                if (object.getId().equals(id)) {
                    visit.stop(object);
                }
            }
        });
    }

    public static Stream<FormComponent> findFormComponentsByType(Form form, SType type) {
        return findOnForm(FormComponent.class, form, fc -> IMInstanciaAwareModel
                .optionalCast(fc.getDefaultModel())
                .map(IMInstanciaAwareModel::getMInstancia)
                .map(SInstance::getType)
                .map(type::equals)
                .orElse(false));
    }

    protected static <T extends Component> Stream<T> findOnForm(Class<T> classOfQuery, Form form, Predicate<T> predicate) {
        final List<T> found = new ArrayList<>();
        form.visitChildren(classOfQuery, new IVisitor<T, Object>() {
            @Override
            public void component(T t, IVisit<Object> visit) {
                if (predicate.test(t)) {
                    found.add(t);
                }
            }
        });
        return found.stream();
    }

}
