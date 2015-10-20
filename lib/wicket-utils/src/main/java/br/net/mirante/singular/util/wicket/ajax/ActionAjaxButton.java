package br.net.mirante.singular.util.wicket.ajax;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.util.MetronicUiBlockerAjaxCallListener;
import br.net.mirante.singular.util.wicket.util.WicketEventUtils;

public abstract class ActionAjaxButton extends AjaxButton {

    private MetronicUiBlockerAjaxCallListener metronicUiBlocker;

    public ActionAjaxButton(String id, Form<?> form) {
        super(id, form);
    }

    public ActionAjaxButton(String id, IModel<String> model, Form<?> form) {
        super(id, model, form);
    }

    public ActionAjaxButton(String id, IModel<String> model) {
        super(id, model);
    }

    public ActionAjaxButton(String id) {
        super(id);
    }

    protected abstract void onAction(AjaxRequestTarget target, Form<?> form);

    public void setMetronicUiBlocker(MetronicUiBlockerAjaxCallListener metronicUiBlocker) {
        this.metronicUiBlocker = metronicUiBlocker;
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        super.onSubmit(target, form);
        onAction(target, form);
    }

    @Override
    protected void onError(AjaxRequestTarget target, Form<?> form) {
        WicketEventUtils.sendAjaxErrorEvent(this, target);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);
        if (metronicUiBlocker != null) {
            attributes.getAjaxCallListeners().add(metronicUiBlocker);
        }
    }
}
