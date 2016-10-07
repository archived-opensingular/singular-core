/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.helpers;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static List<Component> findTag(MarkupContainer container,
                                          Class<? extends Component> tag) {
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
        return findOnForm(FormComponent.class, form, fc -> ISInstanceAwareModel
                .optionalCast(fc.getDefaultModel())
                .map(ISInstanceAwareModel::getMInstancia)
                .map(SInstance::getType)
                .map(type::equals)
                .orElse(false));
    }

    public static <T extends Component> Stream<T> findOnForm(Class<T> classOfQuery, Form form, Predicate<T> predicate) {
        final List<T> found = new ArrayList<>();
        form.visitChildren(classOfQuery, new IVisitor<T, Object>() {
            @Override
            public void component(T t, IVisit<Object> visit) {
                if (predicate == null || predicate.test(t)) {
                    found.add(t);
                }
            }
        });
        return found.stream();
    }

}
