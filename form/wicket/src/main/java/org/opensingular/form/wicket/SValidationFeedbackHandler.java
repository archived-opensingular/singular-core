package org.opensingular.form.wicket;

import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;

import com.google.common.collect.Sets;

import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstances;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class SValidationFeedbackHandler implements Serializable {

    static final MetaDataKey<SValidationFeedbackHandler> MDK = new MetaDataKey<SValidationFeedbackHandler>() {
    };

    private final Component targetComponent;
    private final List<IValidationError>                    currentErrors  = new ArrayList<>();
    private final List<ISValidationFeedbackHandlerListener> listeners      = new ArrayList<>(1);
    private final List<IModel<? extends SInstance>>         instanceModels = new ArrayList<>();

    private SValidationFeedbackHandler(Component targetComponent) {
        this.targetComponent = targetComponent;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // BIND
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static SValidationFeedbackHandler bindTo(Component component) {
        if (isBound(component)) {
            return get(component);
        } else {
            SValidationFeedbackHandler handler = new SValidationFeedbackHandler(component);
            component.setMetaData(MDK, handler);
            return handler;
        }
    }

    public static boolean isBound(Component component) {
        return get(component) != null;
    }

    public static SValidationFeedbackHandler get(Component component) {
        return component.getMetaData(MDK);
    }

    public static Optional<SValidationFeedbackHandler> findNearest(Component component) {
        List<Component> list = new ArrayList<>();
        list.add(component);
        WicketUtils.appendListOfParents(list, component, null);
        return list.stream()
                .filter(it -> isBound(it))
                .map(it -> get(it))
                .findFirst();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CONFIG
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SValidationFeedbackHandler addInstanceModel(IModel<? extends SInstance> instanceModel) {
        this.instanceModels.add(instanceModel);
        return this;
    }

    public SValidationFeedbackHandler addInstanceModels(List<IModel<? extends SInstance>> instanceModels) {
        this.instanceModels.addAll(instanceModels);
        return this;
    }

    public SValidationFeedbackHandler addListener(ISValidationFeedbackHandlerListener listener) {
        this.listeners.add(listener);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void clearValidationMessages(Optional<AjaxRequestTarget> target) {
        updateValidationMessages(target, Collections.emptyList());
    }

    public void updateValidationMessages(Optional<AjaxRequestTarget> target) {
        List<IValidationError> newErrors = collectNestedErrors();
        updateValidationMessages(target, newErrors);
    }

    protected void updateValidationMessages(Optional<AjaxRequestTarget> target, Collection<IValidationError> newErrors) {
        List<IValidationError> oldErrors = new ArrayList<>(currentErrors);

        this.currentErrors.clear();
        this.currentErrors.addAll(newErrors);

        if (!oldErrors.equals(newErrors)) {
            fireFeedbackChanged(
                    target,
                    this.targetComponent,
                    resolveRootInstances(this.targetComponent),
                    oldErrors,
                    newErrors);
        }
    }

    private void fireFeedbackChanged(Optional<AjaxRequestTarget> target,
                                     Component container,
                                     Collection<SInstance> baseInstances,
                                     Collection<IValidationError> oldErrors,
                                     Collection<IValidationError> newErrors) {

        for (ISValidationFeedbackHandlerListener listener : listeners)
            listener.onFeedbackChanged(this, target, container, baseInstances, oldErrors, newErrors);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COLLECT
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public List<IValidationError> collectNestedErrors() {
        return collectNestedErrors(this.targetComponent, IPredicate.all());
    }

    public static List<IValidationError> collectNestedErrors(Component subContainer) {
        return collectNestedErrors(subContainer, IPredicate.all());
    }

    public List<IValidationError> collectNestedErrors(ValidationErrorLevel level) {
        return collectNestedErrors(this.targetComponent, it -> level.ge(it.getErrorLevel()));
    }

    public static List<IValidationError> collectNestedErrors(Component subContainer, ValidationErrorLevel level) {
        return collectNestedErrors(subContainer, it -> level.ge(it.getErrorLevel()));
    }

    public List<IValidationError> collectNestedErrors(IPredicate<IValidationError> filter) {
        return collectNestedErrors(this.targetComponent, resolveRootInstances(this.targetComponent), filter);
    }

    public static List<IValidationError> collectNestedErrors(Component subContainer, IPredicate<IValidationError> filter) {
        return collectNestedErrors(subContainer, resolveRootInstances(subContainer), filter);
    }

    public boolean containsNestedErrors() {
        return containsNestedErrors(this.targetComponent, IPredicate.all());
    }

    public boolean containsNestedErrors(Component subContainer) {
        return containsNestedErrors(subContainer, IPredicate.all());
    }

    public boolean containsNestedErrors(ValidationErrorLevel level) {
        return containsNestedErrors(this.targetComponent, it -> level.ge(it.getErrorLevel()));
    }

    public boolean containsNestedErrors(Component subContainer, ValidationErrorLevel level) {
        return containsNestedErrors(subContainer, it -> level.ge(it.getErrorLevel()));
    }

    public boolean containsNestedErrors(IPredicate<IValidationError> filter) {
        return containsNestedErrors(this.targetComponent, filter);
    }

    public boolean containsNestedErrors(Component subContainer, IPredicate<IValidationError> filter) {
        return containsNestedErrors(subContainer, resolveRootInstances(subContainer), filter);
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel() {
        return findNestedErrorsMaxLevel(this.targetComponent, IPredicate.all());
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel(Component subContainer) {
        return findNestedErrorsMaxLevel(subContainer, IPredicate.all());
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel(IPredicate<IValidationError> filter) {
        return findNestedErrorsMaxLevel(this.targetComponent, filter);
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel(Component subContainer, IPredicate<IValidationError> filter) {
        return collectNestedErrors(subContainer, resolveRootInstances(subContainer), filter).stream()
                .map(it -> it.getErrorLevel())
                .collect(Collectors.maxBy(Comparator.naturalOrder()));
    }

    private static List<IValidationError> collectNestedErrors(Component rootContainer, Collection<SInstance> rootInstances, IPredicate<IValidationError> filter) {

        final List<IValidationError> result = new ArrayList<>();

        for (SInstance rootInstance : rootInstances) {
            final SDocument                document            = rootInstance.getDocument();
            final Set<? extends SInstance> lowerBoundInstances = collectLowerBoundInstances(rootContainer);

            SInstances.visit(rootInstance, (i, v) -> {
                if (lowerBoundInstances.contains(i)) {
                    v.dontGoDeeper();
                } else {
                    document.getValidationErrors(i.getId()).stream()
                            .filter(it -> (filter == null) ? true : filter.test(it))
                            .forEach(it -> result.add(it));
                }
            });
        }

        return result;
    }

    private static boolean containsNestedErrors(Component rootContainer, Collection<SInstance> rootInstances, IPredicate<IValidationError> filter) {
        for (SInstance rootInstance : rootInstances) {

            final SDocument                document            = rootInstance.getDocument();
            final Set<? extends SInstance> lowerBoundInstances = collectLowerBoundInstances(rootContainer);

            Optional<IValidationError> f = SInstances.visit(rootInstance, (i, v) -> {
                if (lowerBoundInstances.contains(i)) {
                    v.dontGoDeeper();
                } else {
                    Optional<IValidationError> found = document.getValidationErrors(i.getId()).stream()
                            .filter(it -> (filter == null) ? true : filter.test(it))
                            .findAny();
                    if (found.isPresent())
                        v.stop(found.get());
                }
            });
            if (f.isPresent())
                return true;
        }
        return false;
    }

    protected static Set<? extends SInstance> collectLowerBoundInstances(Component rootContainer) {
        // coleta os componentes descendentes que possuem um handler, e as instancias correspondentes
        final Set<Component> lowerBoundComponents = Sets.newHashSet();
        if (rootContainer instanceof MarkupContainer) {
            Visits.visitChildren((MarkupContainer) rootContainer, (Component object, IVisit<Void> visit) -> {
                SValidationFeedbackHandler handler = get(object);
                if (handler != null) {
                    visit.dontGoDeeper();
                    lowerBoundComponents.add(object);
                }
            });
        }
        final Set<? extends SInstance> lowerBoundInstances = lowerBoundComponents.stream()
                .flatMap(it -> resolveRootInstances(it).stream())
                .collect(toSet());
        return lowerBoundInstances;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UTILITY
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Collection<SInstance> resolveRootInstances(Component rootContainer) {

        final SValidationFeedbackHandler rootHandler  = get(rootContainer);
        final List<SInstance>            rootInstance = new ArrayList<>();

        if (rootHandler != null) {
            rootHandler
                    .instanceModels
                    .stream()
                    .filter(it -> it != null && it.getObject() != null)
                    .map(IModel::getObject)
                    .forEach(rootInstance::add);
        }

        if (rootInstance.isEmpty()) {
            ISInstanceAwareModel
                    .optionalCast(rootContainer.getDefaultModel())
                    .map(ISInstanceAwareModel::getMInstancia)
                    .ifPresent(rootInstance::add);
        }

        return rootInstance;
    }
}
