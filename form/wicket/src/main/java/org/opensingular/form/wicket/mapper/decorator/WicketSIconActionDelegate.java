package org.opensingular.form.wicket.mapper.decorator;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.ObjectUtils.*;
import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.decorator.action.SInstanceAction.ActionsFactory;
import org.opensingular.form.decorator.action.SInstanceAction.FormDelegate;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.panel.ICloseModalEvent;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.ref.Out;
import org.opensingular.lib.commons.util.HTMLUtil;

/**
 * Implementação de <code>SInstanceAction.Delegate</code> integrada com a infraestrutura Wicket.
 */
public class WicketSIconActionDelegate implements SInstanceAction.Delegate, Serializable {

    private IModel<? extends SInstance> instanceModel;
    private transient List<?>           contextList;
    private List<?>                     serializableContextList;

    public WicketSIconActionDelegate(IModel<? extends SInstance> instanceModel, List<?> contextList) {
        this.instanceModel = instanceModel;
        this.contextList = contextList;
        this.serializableContextList = contextList.stream()
            .filter(it -> it instanceof Serializable)
            .collect(toList());
    }

    /*
     * 
     */
    @Override
    public ISupplier<SInstance> getInstanceRef() {
        return instanceModel::getObject;
    }

    @Override
    public void openForm(
        Out<FormDelegate> formDelegate,
        String title,
        Serializable text,
        ISupplier<SInstance> formInstance,
        ActionsFactory actionsFactory) {

        IModel<? extends SInstance> formInstanceModel = Optional.ofNullable(formInstance)
            .map(it -> it.get())
            .map(it -> new SInstanceRootModel<>(it))
            .orElse(null);
        ISupplier<String> textProvider = () -> (text instanceof String)
            ? HTMLUtil.escapeHtml((String) text)
            : Objects.toString(text, "");
        SInstanceActionOpenModalEvent evt = new SInstanceActionOpenModalEvent(
            title,
            getInternalContext(AjaxRequestTarget.class).orElse(null),
            $m.get(textProvider),
            instanceModel,
            formInstanceModel,
            () -> actionsFactory.getActions(formDelegate.get()));
        formDelegate.set(new FormDelegateImpl(
            getInternalContext(Component.class).orElse(null),
            formInstanceModel));
        getInternalContext(Component.class)
            .ifPresent(comp -> comp.send(comp, Broadcast.BUBBLE, evt));
    }

    @Override
    public void refreshFieldForInstance(SInstance instance) {
        Optional<AjaxRequestTarget> optTarget = getInternalContext(AjaxRequestTarget.class);
        Optional<Component> optComp = getInternalContext(Component.class);
        if (optTarget.isPresent() && optComp.isPresent()) {
            AjaxRequestTarget target = optTarget.get();
            Component comp = optComp.get();

            target.add(
                WicketFormUtils.normalizeComponentsToAjaxRefresh(
                    WicketFormUtils.streamChildrenByInstance(comp.getPage(), instance)
                        .collect(toSet())));
        }
    }

    protected AjaxRequestTarget getAjaxRequestTarget() {
        return getInternalContext(AjaxRequestTarget.class).orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getInternalContext(Class<T> clazz) {
        List<? extends Object> list = firstNonNull(contextList, serializableContextList);
        return list.stream()
            .filter(it -> clazz.isAssignableFrom(it.getClass()))
            .map(it -> (T) it)
            .findFirst();
    }

    /*
     * AS CLASSES ABAIXO NÃO SÃO LAMBDAS PARA MANTER O CONTROLE DAS REFERÊNCIAS,
     * POIS O DELEGATE NÃO É SERIALIZÁVEL!!!
     */

    private static final class FormDelegateImpl implements FormDelegate {
        private final Component                   component;
        private final IModel<? extends SInstance> formInstanceModel;
        public FormDelegateImpl(Component component, IModel<? extends SInstance> formInstanceModel) {
            this.component = component;
            this.formInstanceModel = formInstanceModel;
        }
        @Override
        public void close() {
            Predicate<Component> predicate = it -> Objects.equals(
                it.getDefaultModelObject(),
                (formInstanceModel == null) ? null : formInstanceModel.getObject());

            ICloseModalEvent evt = ICloseModalEvent.of(findAjaxRequestTarget(), predicate);
            if (component != null)
                component.send(component, Broadcast.BUBBLE, evt);
        }
        @Override
        public SInstance getFormInstance() {
            return formInstanceModel.getObject();
        }
        private AjaxRequestTarget findAjaxRequestTarget() {
            return (RequestCycle.get() != null) ? RequestCycle.get().find(AjaxRequestTarget.class) : null;
        }
    }
}
