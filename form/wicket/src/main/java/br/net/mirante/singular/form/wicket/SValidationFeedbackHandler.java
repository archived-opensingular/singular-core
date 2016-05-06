package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SInstances;
import br.net.mirante.singular.form.validation.IValidationError;
import com.google.common.collect.Sets;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.Visits;

import java.io.Serializable;
import java.util.*;

import static java.util.stream.Collectors.toSet;

public class SValidationFeedbackHandler implements Serializable {

    public static final MetaDataKey<SValidationFeedbackHandler> MDK = new MetaDataKey<SValidationFeedbackHandler>() {};

    private Component                        targetComponent;
    private List<IValidationError>           errors = new ArrayList<>();
    private IValidationErrorsChangedListener onValidationErrorsChanged;
    private Component[]                      refreshOnChange;
    private IModel<? extends SInstance>      instanceModel;

    public void updateValidationMessages(Optional<AjaxRequestTarget> target,
                                         MarkupContainer container,
                                         Collection<IValidationError> errors) {

    }

    public static List<IValidationError> collectNestedErrors(Component rootContainer) {

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

        final SValidationFeedbackHandler rootHandler = rootContainer.getMetaData(MDK);
        final SInstance rootInstance = rootHandler.instanceModel.getObject();

        SInstances.visit(rootInstance, (i, v) -> {});

        return null;
    }

    interface IValidationErrorsChangedListener extends Serializable {
        void onFeedbackChanged(Optional<AjaxRequestTarget> target,
                               MarkupContainer container,
                               IModel<SInstance> baseInstance,
                               Collection<IValidationError> oldErrors,
                               Collection<IValidationError> newErrors);
    }
}
