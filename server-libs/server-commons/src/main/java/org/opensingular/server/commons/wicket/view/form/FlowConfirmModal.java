package org.opensingular.server.commons.wicket.view.form;


import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public interface FlowConfirmModal<T extends PetitionEntity> extends Serializable {

    String getMarkup(String idSuffix);

    BSModalBorder init(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm);

}