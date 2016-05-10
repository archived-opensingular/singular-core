package br.net.mirante.singular.form.wicket;

import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;

import com.google.common.collect.Sets;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SInstances;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.validation.IValidationError;

public class SValidationFeedbackHandler implements Serializable {

    public static final MetaDataKey<SValidationFeedbackHandler> MDK = new MetaDataKey<SValidationFeedbackHandler>() {};

    private final Component              targetComponent;
    private IModel<? extends SInstance>  instanceModel;
    private final List<IValidationError> currentErrors = new ArrayList<>();

    public static SValidationFeedbackHandler bindTo(Component targetComponent) {
        SValidationFeedbackHandler handler = new SValidationFeedbackHandler(targetComponent);
        targetComponent.setMetaData(MDK, handler);
        return handler;
    }

    private SValidationFeedbackHandler(Component targetComponent) {
        this.targetComponent = targetComponent;
    }

    public void clearValidationMessages(Optional<AjaxRequestTarget> target) {
        updateValidationMessages(target, Collections.emptyList());
    }

    public void updateValidationMessages(Optional<AjaxRequestTarget> target, Collection<IValidationError> newErrors) {
        ArrayList<IValidationError> oldErrors = new ArrayList<>(currentErrors);

        this.currentErrors.clear();
        this.currentErrors.addAll(newErrors);

        onValidationErrorsChanged(
            target,
            (MarkupContainer) this.targetComponent,
            resolveRootInstance(this.targetComponent),
            oldErrors, newErrors);
    }

    public void onValidationErrorsChanged(Optional<AjaxRequestTarget> target,
                                          MarkupContainer container,
                                          SInstance baseInstance,
                                          Collection<IValidationError> oldErrors,
                                          Collection<IValidationError> newErrors) {}

    public SValidationFeedbackHandler setInstanceModel(IModel<? extends SInstance> instanceModel) {
        this.instanceModel = instanceModel;
        return this;
    }

    public List<IValidationError> collectNestedErrors() {
        return collectNestedErrors(this.targetComponent, resolveRootInstance(this.targetComponent));
    }
    public boolean containsNestedErrors() {
        return containsNestedErrors(this.targetComponent, resolveRootInstance(this.targetComponent));
    }

    public static List<IValidationError> collectNestedErrors(Component subContainer) {
        return collectNestedErrors(subContainer, resolveRootInstance(subContainer));
    }
    public static List<IValidationError> collectNestedErrors(Component rootContainer, SInstance rootInstance) {

        final SDocument document = rootInstance.getDocument();
        final Set<? extends SInstance> lowerBoundInstances = collectLowerBoundInstances(rootContainer);

        final List<IValidationError> result = new ArrayList<>();
        SInstances.visit(rootInstance, (i, v) -> {
            if (lowerBoundInstances.contains(i)) {
                v.dontGoDeeper();
            } else {
                result.addAll(document.getValidationErrors(i.getId()));
            }
        });

        return result;
    }
    public static boolean containsNestedErrors(Component rootContainer, SInstance rootInstance) {

        final SDocument document = rootInstance.getDocument();
        final Set<? extends SInstance> lowerBoundInstances = collectLowerBoundInstances(rootContainer);

        return Boolean.TRUE.equals(SInstances.visit(rootInstance, (i, v) -> {
            if (lowerBoundInstances.contains(i)) {
                v.dontGoDeeper();
            } else {
                if (!document.getValidationErrors(i.getId()).isEmpty())
                    v.stop(true);
            }
        }));
    }

    protected static Set<? extends SInstance> collectLowerBoundInstances(Component rootContainer) {
        // coleta os componentes descendentes que possuem um handler, e as instancias correspondentes
        final Set<Component> lowerBoundComponents = Sets.newHashSet();
        if (rootContainer instanceof MarkupContainer) {
            Visits.visitChildren((MarkupContainer) rootContainer, (Component object, IVisit<Void> visit) -> {
                SValidationFeedbackHandler handler = object.getMetaData(MDK);
                if (handler != null) {
                    visit.dontGoDeeper();
                    lowerBoundComponents.add(object);
                }
            });
        }
        final Set<? extends SInstance> lowerBoundInstances = lowerBoundComponents.stream()
            .map(it -> it.getMetaData(MDK).instanceModel.getObject())
            .collect(toSet());
        return lowerBoundInstances;
    }

    protected static SInstance resolveRootInstance(Component rootContainer) {
        final SValidationFeedbackHandler rootHandler = rootContainer.getMetaData(MDK);

        SInstance rootInstance = null;

        if (rootHandler != null)
            rootInstance = rootHandler.instanceModel.getObject();

        if (rootInstance == null) {
            Object modelObject = rootContainer.getDefaultModelObject();
            if (modelObject instanceof SInstance)
                rootInstance = (SInstance) modelObject;
        }

        if (rootInstance == null)
            throw new IllegalArgumentException("Could not resolve the root instance");

        return rootInstance;
    }

    public static interface IValidationErrorsChangedListener extends Serializable {
        void onFeedbackChanged(Optional<AjaxRequestTarget> target,
                               MarkupContainer container,
                               IModel<SInstance> baseInstance,
                               Collection<IValidationError> oldErrors,
                               Collection<IValidationError> newErrors);

        public static final IValidationErrorsChangedListener NOOP = (t, c, b, o, n) -> {};
    }
}
