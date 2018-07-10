package org.opensingular.form.wicket.modal;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.OpenModalEvent;

public class OpenSingularFormModalEvent<ST extends SType<SI>, SI extends SInstance> extends OpenModalEvent<SI> {

    public OpenSingularFormModalEvent(AjaxRequestTarget target, IModel<SI> instanceModel, ConfigureCallback<SI> configureCallback) {
        super(target, instanceModel, id -> newBodyContent(id, sfp -> sfp.setInstanceCreator(instanceModel::getObject)), configureCallback);
        setLinkFactory(OpenSingularFormModalEvent::newLink);
        setButtonFactory(OpenSingularFormModalEvent::newButton);
    }

    @SuppressWarnings("unchecked")
    public OpenSingularFormModalEvent(AjaxRequestTarget target, Class<ST> instanceType, ConfigureCallback<SI> configureCallback) {
        super(target, id -> newBodyContent(id, sfp -> sfp.setInstanceFromType(instanceType)), configureCallback);
        setModelExtractor(c -> (IModel<SI>) c.getDefaultModel());
        setLinkFactory(OpenSingularFormModalEvent::newLink);
        setButtonFactory(OpenSingularFormModalEvent::newButton);
    }

    private static Component newBodyContent(String id, IConsumer<SingularFormPanel> formPanelConfigurer) {
        final SingularFormPanel sfp = new SingularFormPanel("formPanel", true);
        formPanelConfigurer.accept(sfp);
        return new TemplatePanel(id, sfp.getInstanceModel(), "<div wicket:id='formPanel'></div>")
            .add(sfp);
    }

    protected static <SI extends SInstance> AjaxLink<SI> newLink(String id, ModalDelegate<SI> delegate, ActionCallback<SI> action) {
        return new AjaxLink<SI>(id, delegate.getModel()) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                WicketFormProcessing.onFormSubmit(delegate.getModalBorder().getModalBody(), target, delegate.getModel(), true);
                action.onAction(target, delegate, delegate.getModel());
            }
        };
    }
    protected static <SI extends SInstance> AjaxButton newButton(String id, ModalDelegate<SI> delegate, ActionCallback<SI> action) {
        return new AjaxButton(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                WicketFormProcessing.onFormSubmit(delegate.getModalBorder().getModalBody(), target, delegate.getModel(), true);
                action.onAction(target, delegate, delegate.getModel());
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                WicketFormProcessing.onFormSubmit(delegate.getModalBorder().getModalBody(), target, delegate.getModel(), true);
                action.onError(target, delegate, delegate.getModel());
            }
        };
    }
}
