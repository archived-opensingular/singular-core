/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.util;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.validation.IValidationError;
import br.net.mirante.singular.form.validation.InstanceValidationContext;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.model.IModel;

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
        container.setMetaData(KEY_IS_CELL_CONTAINER, true);
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
            .findAny()
            .get();
        return streamDescendants(rootContainer)
            .filter(c -> c.getDefaultModel() instanceof IMInstanciaAwareModel<?>)
            .filter(c -> predicate.test(c, instanciaIfAware(c.getDefaultModel()).orElse(null)));
    }
    private static Optional<SInstance> instanciaIfAware(IModel<?> model) {
        return (model instanceof IMInstanciaAwareModel<?>)
            ? Optional.ofNullable(((IMInstanciaAwareModel<?>) model).getMInstancia())
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
            } else if (model instanceof IMInstanciaAwareModel<?>) {
                instance = ((IMInstanciaAwareModel<?>) model).getMInstancia();
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
        return (model instanceof IMInstanciaAwareModel<?>)
            ? Optional.ofNullable(((IMInstanciaAwareModel<?>) model).getMInstancia())
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
        String lastBaseTitle = null;
        for (Component comp : components) {

            SInstance instance = WicketFormUtils.instanciaIfAware(comp.getDefaultModel()).orElse(null);

            Optional<WicketBuildContext> wbc = WicketBuildContext.get(comp);
            if (wbc.isPresent()) {

                Optional<String> title = wbc.get().resolveContainerTitle()
                    .map(it -> StringUtils.trimToNull(it.getObject()));
                if (title.isPresent()) {
                    final String baseTitle = title.get();
                    if (Objects.equal(baseTitle, lastBaseTitle)) {
                        continue;
                    } else {
                        lastBaseTitle = baseTitle;
                    }

                    if ((lastInstance != null) && (instance instanceof SIList<?>)) {
                        SIList<SInstance> lista = (SIList<SInstance>) instance;
                        Iterator<SInstance> iter = lista.iterator();
                        for (int i = 1; iter.hasNext(); i++) {
                            SInstance itemInstance = iter.next();
                            if (lastInstance == itemInstance || lastInstance.isDescendantOf(itemInstance)) {
                                titles.addFirst(baseTitle + " [" + i + "]");
                                break;
                            }
                        }
                    } else {
                        titles.addFirst(baseTitle);
                    }
                }
            }
            lastInstance = instance;
        }

        if (!titles.isEmpty())
            return titles.stream().collect(Collectors.joining(" > "));
        else
            return null;
    }
}
