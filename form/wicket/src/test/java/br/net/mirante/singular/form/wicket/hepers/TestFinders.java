package br.net.mirante.singular.form.wicket.hepers;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import com.google.common.collect.Lists;

public class TestFinders {

    public static Optional<String> findId(MarkupContainer container, String leafName) {
        Iterator<Component> it = container.iterator();
//        System.out.println(container);
//        System.out.println(container.getId());
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

}
