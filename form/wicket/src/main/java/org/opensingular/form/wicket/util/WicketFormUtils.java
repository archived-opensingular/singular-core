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

package org.opensingular.form.wicket.util;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public abstract class WicketFormUtils {
    private static final MetaDataKey<Integer> KEY_INSTANCE_ID = new MetaDataKey<Integer>() {
    };
    private static final MetaDataKey<Boolean> KEY_IS_CELL_CONTAINER = new MetaDataKey<Boolean>() {
    };
    private static final MetaDataKey<MarkupContainer> KEY_CELL_CONTAINER = new MetaDataKey<MarkupContainer>() {
    };
    private static final MetaDataKey<MarkupContainer> KEY_ROOT_CONTAINER = new MetaDataKey<MarkupContainer>() {
    };

    private WicketFormUtils() {
    }

    public static void setRootContainer(Component component, MarkupContainer rootContainer) {
        component.setMetaData(KEY_ROOT_CONTAINER, rootContainer);
    }

    public static MarkupContainer getRootContainer(Component component) {
        return component.getMetaData(KEY_ROOT_CONTAINER);
    }

    public static void setInstanceId(Component component, SInstance instance) {
        component.setMetaData(KEY_INSTANCE_ID, instance.getId());
    }

    public static boolean isForInstance(Component component, SInstance instance) {
        return Objects.equal(component.getMetaData(KEY_INSTANCE_ID), instance.getId());
    }

    public static void markAsCellContainer(MarkupContainer container) {
        container.setMetaData(KEY_IS_CELL_CONTAINER, Boolean.TRUE);
    }

    public static boolean isMarkedAsCellContainer(Component container) {
        return Boolean.TRUE.equals(container.getMetaData(KEY_IS_CELL_CONTAINER));
    }

    public static void setCellContainer(Component component, MarkupContainer container) {
        container.setOutputMarkupId(true);
        component.setMetaData(KEY_CELL_CONTAINER, container);
    }

    public static Optional<MarkupContainer> findCellContainer(Component component) {
        // ele mesmo
        if (isMarkedAsCellContainer(component))
            return Optional.of((MarkupContainer) component);

        // referencia direta
        final MarkupContainer container = component.getMetaData(KEY_CELL_CONTAINER);
        if (container != null)
            return Optional.of(container);

        // busca nos ascendentes
        return streamAscendants(component)
                .filter(WicketFormUtils::isMarkedAsCellContainer)
                .findFirst();
    }


    /**
     * Pesquisa algum componente na hieraquia (incluindo a si mesmo) que possa ser atualizado via Ajax.
     * @param component o componente que deve ser atualizado
     * @return o componente dentro da hieraquia que é atualizavel
     */
    public static Component findUpdatableComponentInHierarchy(Component component) {
        Component comp = component;
        while (comp != null) {
            if (comp.getOutputMarkupId()
                    && comp.hasBeenRendered()
                    && comp.isVisibleInHierarchy()
                    && !(comp instanceof AbstractRepeater)) {
                return comp;
            }
            comp = comp.getParent();
        }
        return component;
    }

    public static MarkupContainer getCellContainer(Component component) {
        return findCellContainer(component).orElse(null);
    }

    public static Optional<Component> findChildByInstance(Component root, SInstance instance) {
        return streamChildrenByInstance(root, instance).findAny();
    }

    public static Stream<Component> streamChildrenByInstance(Component root, SInstance instance) {
        Predicate<? super Component> sameInstanceFilter = c -> instanciaIfAware(c.getDefaultModel())
                .filter(it -> Objects.equal(it.getName(), instance.getName()))
                .filter(it -> Objects.equal(it.getId(), instance.getId()))
                .isPresent();
        return streamDescendants(root)
                .filter(sameInstanceFilter);
    }

    private static Optional<SInstance> instanciaIfAware(IModel<?> model) {
        return (model instanceof ISInstanceAwareModel<?>)
                ? Optional.ofNullable(((ISInstanceAwareModel<?>) model).getSInstance())
                : Optional.ofNullable(model)
                .map(it -> it.getObject())
                .map($L.castOrNull(SInstance.class));
    }

    public static Stream<MarkupContainer> streamAscendants(Component root) {
        return WicketUtils.listParents(root).stream();
    }

    @SuppressWarnings("unchecked")
    public static Stream<Component> streamDescendants(Component root) {
        return Stream.of(root)
                .flatMap(WicketUtils.$L.recursiveIterable(c -> (c instanceof Iterable<?>) ? (Iterable<Component>) c : null));
    }

    public static Optional<SInstance> resolveInstance(Component component) {
        return (component != null)
                ? resolveInstance(component.getDefaultModel())
                : Optional.empty();
    }

    public static Optional<SInstance> resolveInstance(final IModel<?> model) {
        return (model instanceof ISInstanceAwareModel<?>)
                ? Optional.ofNullable(((ISInstanceAwareModel<?>) model).getSInstance())
                : Optional.empty();
    }

    public static String generateTitlePath(Component parentContainer,
                                           SInstance parentContext,
                                           Component childComponent,
                                           SInstance childInstance) {

        List<Component> components = Lists.newArrayList(childComponent);
        WicketUtils.appendListOfParents(components, childComponent, parentContainer);

        Deque<String> titles = new LinkedList<>();
        SInstance lastInstance = null;
        String lastTitle = null;
        for (Component comp : components) {

            SInstance instance = WicketFormUtils.instanciaIfAware(comp.getDefaultModel()).orElse(null);

            String title = findTitle(comp);
            if (title != null && !Objects.equal(title, lastTitle)) {
                lastTitle = title;
                addTitle(titles, title, instance, lastInstance);
            }
            lastInstance = instance;
        }

        if (!titles.isEmpty()) {
            return titles.stream().collect(Collectors.joining(" > "));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static void addTitle(Deque<String> titles, String title, SInstance instance, SInstance lastInstance) {
        if ((lastInstance != null) && (instance instanceof SIList<?>)) {
            int pos = findPos((SIList<SInstance>) instance, lastInstance);
            if (pos != -1) {
                titles.addFirst(title + " [" + pos + "]");
            }
        } else {
            titles.addFirst(title);
        }
    }

    private static int findPos(@Nonnull SIList<SInstance> instance, @Nonnull SInstance lastInstance) {
        int pos = 1;
        for (SInstance itemInstance : instance) {
            if (lastInstance == itemInstance || lastInstance.isDescendantOf(itemInstance)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private static @Nullable
    String findTitle(Component comp) {
        WicketBuildContext wbc = WicketBuildContext.find(comp).orElse(null);
        if (wbc != null) {
            return wbc.resolveContainerTitle().map(it -> StringUtils.trimToNull(it.getObject())).orElse(null);
        }
        return null;

    }

    /**
     * De-duplicate, and replace components with the appropriate parent container, if necessary.
     */
    public static Component[] normalizeComponentsToAjaxRefresh(Collection<Component> children) {
        Collection<Component> set = new HashSet<>();
        for (Component child : children) {
            set.add(child);
            Optional<MarkupContainer> container = findCellContainer(child);
            if (container.isPresent())
                set.add(container.get());
        }
        List<Component> list = new ArrayList<>(set);
        for (int i = list.size() - 1; i >= 0; i--) {
            final Component ic = list.get(i);
            for (int j = list.size() - 1; j >= 0; j--) {
                final Component jc = list.get(j);
                if ((ic != jc) && ic.getPageRelativePath().contains(jc.getPageRelativePath())) {
                    list.remove(i);
                    break;
                }
            }
        }
        Component[] components = list.toArray(new Component[0]);
        return components;
    }

    public static void bubbleInstanceAsEvent(Component comp, SInstance instance) {
        comp.send(comp, Broadcast.BUBBLE, instance);
    }

    public static void breadthInstanceAsEvent(Component comp, SInstance instance) {
        comp.send(comp, Broadcast.BREADTH, instance);
    }
}
