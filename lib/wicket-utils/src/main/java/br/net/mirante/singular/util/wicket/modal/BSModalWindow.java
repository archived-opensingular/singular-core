package br.net.mirante.singular.util.wicket.modal;

import br.net.mirante.singular.util.wicket.lambda.IConsumer;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder.ButtonStyle;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder.Size;

public class BSModalWindow extends Panel {

    private static final String BODY_CONTAINER_ID = "_b";
    private static final String MODAL_ID = "_m";
    private static final String FORM_ID = "_f";

    private final BSModalBorder modalBorder = new BSModalBorder(MODAL_ID);
    private final TemplatePanel bodyContainer = new TemplatePanel(BODY_CONTAINER_ID, p -> "<div wicket:id='" + p.get(0).getId() + "'></div>");
    private MarkupContainer form;
    private boolean resetOnBodySwitch = true;

    public BSModalWindow(String id, IModel<?> model) {
        this(id, model, true);
    }
    public BSModalWindow(String id) {
        this(id, true);
    }
    public BSModalWindow(String id, IModel<?> model, boolean wrapBodyWithForm) {
        super(id, model);
        doInit(wrapBodyWithForm);
    }

    public BSModalWindow(String id, IModel<?> model, boolean wrapBodyWithForm, boolean resetOnBodySwitch) {
        super(id, model);
        this.resetOnBodySwitch = resetOnBodySwitch;
        doInit(wrapBodyWithForm);
    }

    public BSModalWindow(String id, boolean wrapBodyWithForm, boolean resetOnBodySwitch) {
        super(id);
        this.resetOnBodySwitch = resetOnBodySwitch;
        doInit(wrapBodyWithForm);
    }

    public BSModalWindow(String id, boolean wrapBodyWithForm) {
        super(id);
        doInit(wrapBodyWithForm);
    }

    private void doInit(boolean wrapBodyWithForm) {
        form = (wrapBodyWithForm) ? new Form<>(FORM_ID) : new NonForm(FORM_ID);
        this
            .add(form
                .add(modalBorder
                    .add(bodyContainer)));
        setBody(new WebMarkupContainer("_"));
    }

    public BSModalWindow setBody(Component body) {
        bodyContainer.removeAll();
        bodyContainer.add(body);
        if (resetOnBodySwitch) {
            removeButtons();
            getModalBorder()
                .setSize(Size.NORMAL)
                .setDismissible(false);
        }
        return this;
    }

    public BSModalBorder getModalBorder() {
        return modalBorder;
    }

    public Form<?> getForm() {
        return (Form<?>) form;
    }

    public void show(AjaxRequestTarget target) {
        getModalBorder().show(target);
    }
    public void hide(AjaxRequestTarget target) {
        getModalBorder().hide(target);
    }

    public BSModalWindow removeButtons() {
        getModalBorder().removeButtons();
        return this;
    }
    
    public BSModalWindow setTitleText(IModel<String> titleModel) {
        getModalBorder().setTitleText(titleModel);
        return this;
    }

    public BSModalWindow setSize(Size size) {
        getModalBorder().setSize(size);
        return this;
    }

    public BSModalWindow addButton(ButtonStyle style, ActionAjaxButton button) {
        getModalBorder().addButton(style, button);
        return this;
    }

    public BSModalWindow addButton(ButtonStyle style, IModel<String> label, ActionAjaxButton button) {
        getModalBorder().addButton(style, label, button);
        return this;
    }

    public <T> BSModalWindow addLink(ButtonStyle style, IModel<String> label, ActionAjaxLink<T> button) {
        getModalBorder().addLink(style, label, button);
        return this;
    }

    public <T> BSModalWindow addLink(ButtonStyle style, ActionAjaxLink<T> button) {
        getModalBorder().addLink(style, button);
        return this;
    }

    public <T> BSModalWindow setCloseIconCallback(IConsumer<AjaxRequestTarget> closeIconCallback) {
        getModalBorder().setCloseIconCallback(closeIconCallback);
        return this;
    }

    private static final class NonForm extends WebMarkupContainer {
        private NonForm(String id) {
            super(id);
            setRenderBodyOnly(true);
        }
        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            tag.setName("div");
        }
    }
}
