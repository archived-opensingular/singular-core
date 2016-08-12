package br.net.mirante.singular.server.commons.wicket.view.form;


import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public interface FlowConfirmModalBuilder extends Serializable {

    String getMarkup(String idSuffix);

    BSModalBorder build(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm);

}