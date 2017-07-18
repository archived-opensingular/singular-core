package org.opensingular.form.wicket.mapper.decorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.opensingular.form.SInstance;
import org.opensingular.form.decorator.action.SInstanceAction;
import org.opensingular.form.wicket.mapper.decorator.WicketSIconActionDelegate.ModelGetterSupplier;
import org.opensingular.form.wicket.panel.IOpenModalEvent;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;

import com.google.common.collect.ImmutableList;

/**
 * ESTA CLASSE DE EVENTO NÃO É SERIALIZÁVEL!!!
 * Por isso a classe de botão é estática, para manter o controle das referências. Cuidado com referências implícitas!
 */
final class SInstanceActionOpenModalEvent implements IOpenModalEvent {
    private String                      title;
    private AjaxRequestTarget           target;
    private IModel<? extends SInstance> instanceModel;
    private IModel<? extends SInstance> formInstance;
    private List<SInstanceAction>       actions;

    public SInstanceActionOpenModalEvent(String title,
        AjaxRequestTarget target,
        IModel<? extends SInstance> instanceSupplier,
        IModel<? extends SInstance> formInstanceModel,
        List<SInstanceAction> actions) {
        this.title = title;
        this.target = target;
        this.instanceModel = instanceSupplier;
        this.formInstance = formInstanceModel;
        this.actions = actions;
    }

    @Override
    public String getModalTitle() {
        return this.title;
    }

    @Override
    public AjaxRequestTarget getTarget() {
        return this.target;
    }

    @Override
    public Component getBodyContent(String id) {
        return new TemplatePanel(id, "<div wicket:id='panel'></div>")
            .add(new SingularFormPanel("panel", new ModelGetterSupplier<SInstance>(formInstance)))
            .setDefaultModel(formInstance);
    }
    @Override
    public Iterator<ButtonDef> getFooterButtons(IConsumer<AjaxRequestTarget> closeCallback) {
        final List<ButtonDef> buttons = new ArrayList<IOpenModalEvent.ButtonDef>();
        for (int i = 0; i < actions.size(); i++) {
            final SInstanceAction action = actions.get(i);

            final ButtonStyle style = WicketSIconActionDelegate.resolveButtonStyle(action.getType());
            final Model<String> label = Model.of(action.getText());
            final FooterButton button = new FooterButton("action" + i, action, instanceModel, formInstance);
            buttons.add(new ButtonDef(style, label, button));
        }
        return buttons
            .iterator();
    }

    static final class FooterButton extends ActionAjaxButton {

        private final IModel<? extends SInstance> instanceSupplier;
        private final IModel<? extends SInstance> formInstanceModel;
        private final SInstanceAction             action;

        private FooterButton(String id,
            SInstanceAction action,
            IModel<? extends SInstance> instanceSupplier,
            IModel<? extends SInstance> formInstanceModel) {
            super(id);
            this.action = action;
            this.instanceSupplier = instanceSupplier;
            this.formInstanceModel = formInstanceModel;
        }
        @Override
        protected void onAction(AjaxRequestTarget target, Form<?> form) {
            List<Object> childContextList = ImmutableList.of(
                target,
                form,
                formInstanceModel,
                formInstanceModel.getObject(),
                this);
            action.getActionHandler().onAction(
                action,
                new ModelGetterSupplier<SInstance>(formInstanceModel),
                new WicketSIconActionDelegate(
                    instanceSupplier,
                    childContextList));
        }
    }
}