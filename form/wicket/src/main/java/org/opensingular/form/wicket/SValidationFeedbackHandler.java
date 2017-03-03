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

package org.opensingular.form.wicket;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstances;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.validation.IValidationError;
import org.opensingular.form.validation.ValidationErrorLevel;
import org.opensingular.form.wicket.feedback.FeedbackFence;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.wicket.util.util.Shortcuts;
import org.opensingular.lib.wicket.util.util.WicketUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

public class SValidationFeedbackHandler implements Serializable {

    static final MetaDataKey<SValidationFeedbackHandler> MDK = new MetaDataKey<SValidationFeedbackHandler>() {
    };

    private final FeedbackFence feedbackFence;
    private final List<IValidationError> currentErrors = new ArrayList<>();
    private final List<ISValidationFeedbackHandlerListener> listeners = new ArrayList<>(1);
    private IModel<? extends List<IModel<? extends SInstance>>> instanceModels = $m.ofValue(new ArrayList<>());

    private SValidationFeedbackHandler(FeedbackFence feedbackFence) {
        this.feedbackFence = feedbackFence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // BIND
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static SValidationFeedbackHandler bindTo(FeedbackFence fence) {
        Component component = fence.getMainContainer();
        if (isBound(component)) {
            return get(component);
        } else {
            SValidationFeedbackHandler handler = new SValidationFeedbackHandler(fence);
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
                .filter(SValidationFeedbackHandler::isBound)
                .map(SValidationFeedbackHandler::get)
                .findFirst();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CONFIG
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SValidationFeedbackHandler addInstanceModel(IModel<? extends SInstance> instanceModel) {
        this.instanceModels.getObject().add(instanceModel);
        return this;
    }

    public SValidationFeedbackHandler addInstanceModels(List<IModel<? extends SInstance>> instanceModels) {
        this.instanceModels.getObject().addAll(instanceModels);
        return this;
    }

    public SValidationFeedbackHandler setInstanceModels(IModel<? extends List<IModel<? extends SInstance>>> instanceModels) {
        this.instanceModels = (instanceModels != null) ? instanceModels : $m.ofValue(new ArrayList<>());
        return this;
    }

    public SValidationFeedbackHandler addListener(ISValidationFeedbackHandlerListener listener) {
        this.listeners.add(listener);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void clearValidationMessages(AjaxRequestTarget target) {
        updateValidationMessages(target, Collections.emptyList());
    }

    public void updateValidationMessages(AjaxRequestTarget target) {
        List<IValidationError> newErrors = collectNestedErrors();
        updateValidationMessages(target, newErrors);
    }

    protected void updateValidationMessages(AjaxRequestTarget target, Collection<IValidationError> newErrors) {
        List<IValidationError> oldErrors = new ArrayList<>(currentErrors);

        this.currentErrors.clear();
        this.currentErrors.addAll(newErrors);

        if (!oldErrors.equals(newErrors)) {
            fireFeedbackChanged(
                    target,
                    this.feedbackFence.getMainContainer(),
                    resolveRootInstances(this.feedbackFence.getMainContainer()),
                    oldErrors,
                    newErrors);
        }
    }

    private void fireFeedbackChanged(AjaxRequestTarget target,
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
        return collectNestedErrors(feedbackFence, IPredicate.all());
    }

    public static List<IValidationError> collectNestedErrors(FeedbackFence feedbackFence) {
        return collectNestedErrors(feedbackFence, IPredicate.all());
    }

    public List<IValidationError> collectNestedErrors(ValidationErrorLevel level) {
        return collectNestedErrors(this.feedbackFence, it -> level.ge(it.getErrorLevel()));
    }

    public static List<IValidationError> collectNestedErrors(FeedbackFence feedbackFence, ValidationErrorLevel level) {
        return collectNestedErrors(feedbackFence, it -> level.ge(it.getErrorLevel()));
    }

    public List<IValidationError> collectNestedErrors(IPredicate<IValidationError> filter) {
        return collectNestedErrors(this.feedbackFence, resolveRootInstances(this.feedbackFence.getMainContainer()), filter);
    }

    public static List<IValidationError> collectNestedErrors(FeedbackFence feedbackFence, IPredicate<IValidationError> filter) {
        return collectNestedErrors(feedbackFence, resolveRootInstances(feedbackFence.getMainContainer()), filter);
    }

    public boolean containsNestedErrors() {
        return containsNestedErrors(this.feedbackFence, IPredicate.all());
    }

    public boolean containsNestedErrors(FeedbackFence feedbackFence) {
        return containsNestedErrors(feedbackFence, IPredicate.all());
    }

    public boolean containsNestedErrors(ValidationErrorLevel level) {
        return containsNestedErrors(this.feedbackFence, it -> level.ge(it.getErrorLevel()));
    }

    public boolean containsNestedErrors(FeedbackFence feedbackFence, ValidationErrorLevel level) {
        return containsNestedErrors(feedbackFence, it -> level.ge(it.getErrorLevel()));
    }

    public boolean containsNestedErrors(IPredicate<IValidationError> filter) {
        return containsNestedErrors(this.feedbackFence, filter);
    }

    public boolean containsNestedErrors(FeedbackFence feedbackFence, IPredicate<IValidationError> filter) {
        return containsNestedErrors(feedbackFence, resolveRootInstances(feedbackFence.getMainContainer()), filter);
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel() {
        return findNestedErrorsMaxLevel(this.feedbackFence, IPredicate.all());
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel(FeedbackFence feedbackFence) {
        return findNestedErrorsMaxLevel(feedbackFence, IPredicate.all());
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel(IPredicate<IValidationError> filter) {
        return findNestedErrorsMaxLevel(this.feedbackFence, filter);
    }

    public Optional<ValidationErrorLevel> findNestedErrorsMaxLevel(FeedbackFence feedbackFence, IPredicate<IValidationError> filter) {
        return collectNestedErrors(feedbackFence, resolveRootInstances(feedbackFence.getMainContainer()), filter).stream()
                .map(IValidationError::getErrorLevel)
                .collect(Collectors.maxBy(Comparator.naturalOrder()));
    }

    private static List<IValidationError> collectNestedErrors(FeedbackFence feedbackFence, Collection<SInstance> rootInstances, IPredicate<IValidationError> filter) {

        final List<IValidationError> result = new ArrayList<>();

        for (SInstance rootInstance : rootInstances) {
            final SDocument                document            = rootInstance.getDocument();
            final Set<? extends SInstance> lowerBoundInstances = collectLowerBoundInstances(feedbackFence);

            SInstances.visit(rootInstance, (i, v) -> {
                if (lowerBoundInstances.contains(i)) {
                    v.dontGoDeeper();
                } else {
                    document.getValidationErrors(i.getId()).stream()
                            .filter(it -> (filter == null) || filter.test(it))
                            .forEach(result::add);
                }
            });
        }

        return result;
    }

    private static boolean containsNestedErrors(FeedbackFence feedbackFence, Collection<SInstance> rootInstances, IPredicate<IValidationError> filter) {
        for (SInstance rootInstance : rootInstances) {

            final SDocument                document            = rootInstance.getDocument();
            final Set<? extends SInstance> lowerBoundInstances = collectLowerBoundInstances(feedbackFence);

            Optional<IValidationError> f = SInstances.visit(rootInstance, (i, v) -> {
                if (lowerBoundInstances.contains(i)) {
                    v.dontGoDeeper();
                } else {
                    Optional<IValidationError> found = document.getValidationErrors(i.getId()).stream()
                            .filter(it -> (filter == null) || filter.test(it))
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

    protected static Set<? extends SInstance> collectLowerBoundInstances(FeedbackFence feedbackFence) {

        // coleta os componentes descendentes que possuem um handler, e as instancias correspondentes
        final Set<Component> mainComponents     = collectLowerBoundInstances(feedbackFence.getMainContainer());
        final Set<Component> externalComponents = collectLowerBoundInstances(feedbackFence.getExternalContainer());

        return ((Collection<Component>) CollectionUtils.disjunction(mainComponents, externalComponents)).stream()
                .flatMap(it -> resolveRootInstances(it).stream())
                .collect(toSet());
    }

    private static Set<Component> collectLowerBoundInstances(Component container) {
        final Set<Component> comps = Sets.newHashSet();
        if (container instanceof MarkupContainer) {
            Visits.visitChildren((MarkupContainer) container, (Component object, IVisit<Void> visit) -> {
                SValidationFeedbackHandler handler = get(object);
                if (handler != null) {
                    visit.dontGoDeeper();
                    comps.add(object);
                }
            });
        }
        return comps;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UTILITY
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Collection<SInstance> resolveRootInstances(Component rootContainer) {

        final SValidationFeedbackHandler rootHandler  = get(rootContainer);
        final List<SInstance>            rootInstance = new ArrayList<>();

        if (rootHandler != null) {
            rootHandler.instanceModels.getObject()
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
