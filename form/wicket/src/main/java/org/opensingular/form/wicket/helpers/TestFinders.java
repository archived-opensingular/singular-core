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

import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
public class TestFinders {

    private TestFinders() {}

    public static Optional<String> findId(MarkupContainer container, String leafName) {
        for (Component component : container) {
            if (component.getId().equals(leafName)) {
                return Optional.of(leafName);
            } else if (component instanceof MarkupContainer) {
                Optional<String> found = findId((MarkupContainer) component, leafName);
                if (found.isPresent()) {
                    return Optional.of(component.getId() + ":" + found.get());
                }
            }
        }
        return Optional.empty();
    }   

    public static <T extends Component> List<T> findTag(MarkupContainer container, String id, Class<T> tag) {
        List<T> tags = findTag(container, tag);
        return tags.stream().filter((c) -> c.getId().equals(id)).collect(Collectors.toList());
    }

    public static <T extends Component> List<T> findTag(MarkupContainer container, Class<T> tag) {
        List<T> result = Lists.newArrayList();
        findTag(tag, result, container);
        return result;
    }
    
    private static <T extends  Component> void findTag(Class<T> tag, List<T> result, MarkupContainer container) {
        for (Component component : container) {
            if (tag.isInstance(component)) {
                result.add(tag.cast(component));
            } else if (component instanceof MarkupContainer) {
                findTag(tag, result, (MarkupContainer) component);
            }
        }
    }
    
    public static Component findFirstComponentWithId(MarkupContainer container, String id) {
        return container.visitChildren(Component.class, (IVisitor<Component, Component>) (object, visit) -> {
            if (object.getId().equals(id)) {
                visit.stop(object);
            }
        });
    }

    public static Stream<FormComponent> findFormComponentsByType(Form form, SType type) {
        return findOnForm(FormComponent.class, form, fc -> ISInstanceAwareModel
                .optionalCast(fc.getDefaultModel())
                .map(ISInstanceAwareModel::getMInstancia)
                .map(SInstance::getType)
                .map(type::equals)
                .orElse(Boolean.FALSE));
    }

    public static <T extends Component> Stream<T> findOnForm(Class<T> classOfQuery, Form form, Predicate<T> predicate) {
        final List<T> found = new ArrayList<>();
        form.visitChildren(classOfQuery, (IVisitor<T, Object>) (t, visit) -> {
            if (predicate == null || predicate.test(t)) {
                found.add(t);
            }
        });
        return found.stream();
    }

}
