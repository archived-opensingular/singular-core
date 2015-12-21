package br.net.mirante.singular.form.wicket.component;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class BelverSaveButton extends BelverValidationButton {

    public BelverSaveButton(String id, IModel<MInstancia> currentInstance) {
        super(id, currentInstance);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
        if (addValidationErrors(form, getCurrentInstance().getObject())) {
            MElement rootXml = MformPersistenciaXML.toXML(getCurrentInstance().getObject());
            handleSaveXML(target, rootXml);
        }
        target.add(form);
    }

    protected abstract void handleSaveXML(AjaxRequestTarget target, MElement xml);
}

