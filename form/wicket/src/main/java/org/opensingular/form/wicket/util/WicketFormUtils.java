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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class WicketFormUtils {
    private static final MetaDataKey<Integer>         KEY_INSTANCE_ID       = new MetaDataKey<Integer>() {};
    private static final MetaDataKey<Boolean>         KEY_IS_CELL_CONTAINER = new MetaDataKey<Boolean>() {};
    private static final MetaDataKey<MarkupContainer> KEY_CELL_CONTAINER    = new MetaDataKey<MarkupContainer>() {};
    private static final MetaDataKey<MarkupContainer> KEY_ROOT_CONTAINER    = new MetaDataKey<MarkupContainer>() {};

    private WicketFormUtils() {}

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
    public static Component resolveRefreshingComponent(Component component) {
        Component comp = component;
        while ((comp != null) && (comp.getParent() != null) && (!comp.getParent().isVisibleInHierarchy()))
            comp = comp.getParent();
        return comp;
    }
    public static MarkupContainer getCellContainer(Component component) {
        return findCellContainer(component).orElse(null);
    }

    public static Optional<Component> findChildByInstance(Component root, SInstance instance) {
        return streamDescendants(root)
            .filter(c -> instanciaIfAware(c.getDefaultModel()).orElse(null) == instance)
            .findAny();
    }
    public static Stream<Component> streamComponentsByInstance(Component anyComponent, BiPredicate<Component, SInstance> predicate) {
        MarkupContainer rootContainer = streamAscendants(anyComponent)
                .map(c -> getRootContainer(c))
                .filter(c -> c != null)
                .findAny().orElse(null);
        if (rootContainer == null) {
            return null;
        }
        return streamDescendants(rootContainer)
            .filter(c -> c.getDefaultModel() instanceof ISInstanceAwareModel<?>)
            .filter(c -> predicate.test(c, instanciaIfAware(c.getDefaultModel()).orElse(null)));
    }
    private static Optional<SInstance> instanciaIfAware(IModel<?> model) {
        return (model instanceof ISInstanceAwareModel<?>)
            ? Optional.ofNullable(((ISInstanceAwareModel<?>) model).getSInstance())
            : Optional.empty();
    }

    public static Stream<MarkupContainer> streamAscendants(Component root) {
        return WicketUtils.listParents(root).stream();
    }

    @SuppressWarnings("unchecked")
    public static Stream<Component> streamDescendants(Component root) {
        return Stream.of(root)
            .flatMap(WicketUtils.$L.recursiveIterable(c -> (c instanceof Iterable<?>) ? (Iterable<Component>) c : null));
    }

    public static void associateErrorsToComponents(InstanceValidationContext validationContext, MarkupContainer container) {

        final Map<Integer, Collection<IValidationError>> instanceErrors = validationContext.getErrorsByInstanceId();

        container.visitChildren((component, visit) -> {
            final IModel<?> model = component.getDefaultModel();
            final SInstance instance;
            if (model == null) {
                return;
            } else if (model.getObject() instanceof SInstance) {
                instance = (SInstance) model.getObject();
            } else if (model instanceof ISInstanceAwareModel<?>) {
                instance = ((ISInstanceAwareModel<?>) model).getSInstance();
            } else {
                return;
            }

            final Collection<IValidationError> errors = instanceErrors.get(instance.getId());
            if (errors != null) {
                for (IValidationError error : errors) {
                    switch (error.getErrorLevel()) {
                        case ERROR:
                            component.error(error.getMessage());
                            break;
                        case WARNING:
                            component.warn(error.getMessage());
                            break;
                        default:
                            throw new IllegalStateException("Invalid error level: " + error.getErrorLevel());
                    }
                }
            }
        });
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

    @SuppressWarnings("unchecked")
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
            if (title != null && ! Objects.equal(title, lastTitle)) {
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
        for(SInstance itemInstance : instance) {
            if (lastInstance == itemInstance || lastInstance.isDescendantOf(itemInstance)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    private static @Nullable String findTitle(Component comp) {
        WicketBuildContext wbc = WicketBuildContext.find(comp).orElse(null);
        if (wbc != null) {
            return wbc.resolveContainerTitle().map(it -> StringUtils.trimToNull(it.getObject())).orElse(null);
        }
        return null;

    }
}
