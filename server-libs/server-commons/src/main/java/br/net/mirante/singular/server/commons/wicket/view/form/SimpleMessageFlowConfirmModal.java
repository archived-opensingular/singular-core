package br.net.mirante.singular.server.commons.wicket.view.form;

import org.opensingular.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.wicket.builder.HTMLParameters;
import br.net.mirante.singular.server.commons.wicket.builder.MarkupCreator;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


public class SimpleMessageFlowConfirmModal<T extends PetitionEntity> extends AbstractFlowConfirmModal<T> {

    public SimpleMessageFlowConfirmModal(AbstractFormPage<T> formPage) {
        super(formPage);
    }

    @Override
    public String getMarkup(String idSuffix) {
        return MarkupCreator.div("flow-modal" + idSuffix, new HTMLParameters().styleClass("portlet-body form"), MarkupCreator.div("flow-msg"));
    }

    public BSModalBorder init(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        final BSModalBorder modal = new BSModalBorder("flow-modal" + idSuffix, new StringResourceModel("label.button.confirm", formPage, null));
        addDefaultCancelButton(modal);
        addDefaultConfirmButton(tn, im, vm, modal);
        modal.add(new Label("flow-msg", String.format("Tem certeza que deseja %s ?", tn)));
        return modal;
    }

}