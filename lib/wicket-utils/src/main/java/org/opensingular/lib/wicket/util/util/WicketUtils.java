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

package org.opensingular.lib.wicket.util.util;


import org.opensingular.lib.wicket.util.lambda.ILambdasMixin;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class WicketUtils {

    public static final IBehaviorsMixin $b = new IBehaviorsMixin() {};
    public static final IModelsMixin $m = new IModelsMixin() {};
    public static final IValidatorsMixin $v = new IValidatorsMixin() {};
    public static final ILambdasMixin $L = new ILambdasMixin() {};

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
    
    /**
     * Retorna uma lista de parent containers, ordenados do filho para o pai.
     * @param child
     * @return lista de parent containers, ordenados do filho para o pai
     */
    @SuppressWarnings("unchecked")
    public static <C extends MarkupContainer> Optional<C> findClosestParent(Component component, Class<C> parentClass) {
        return listParents(component).stream()
            .filter(it -> parentClass.isAssignableFrom(it.getClass()))
            .map(it -> (C) it)
            .findFirst();
    }

    /**
     * Retorna uma lista de parent containers, ordenados do filho para o pai.
     * @param child
     * @return lista de parent containers, ordenados do filho para o pai
     */
    public static List<MarkupContainer> listParents(Component child) {
        return listParents(child, null);
    }

    /**
     * Retorna uma lista de parent containers, ordenados do filho para o pai.
     * @param child
     * @param topParentInclusive parent mais alto na hierarquia a ser incluído na lista
     * @return 
     */
    public static List<MarkupContainer> listParents(Component child, MarkupContainer topParentInclusive) {
        List<MarkupContainer> list = new ArrayList<>();
        if (child != null) {
            MarkupContainer container = child.getParent();
            while (container != null) {
                list.add(container);
                if (container == topParentInclusive)
                    break;
                container = container.getParent();
            }
            if (topParentInclusive != container) {
                throw new IllegalArgumentException("Top parent not found");
            }
        }
        return list;
    }

    /**
     * Adiciona os parent containers à lista, ordenados do filho para o pai.
     * @param child
     * @param topParentInclusive parent mais alto na hierarquia a ser incluído na lista
     */
    public static void appendListOfParents(List<? super MarkupContainer> list, Component child, Component topParentInclusive) {
        if (child != null) {
            MarkupContainer container = child.getParent();
            while (container != null) {
                list.add(container);
                if (container == topParentInclusive)
                    break;
                container = container.getParent();
            }
            if (topParentInclusive != container) {
                throw new IllegalArgumentException("Top parent not found");
            }
        }
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
        return false;
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
