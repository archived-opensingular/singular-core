package org.opensingular.singular.server.commons.wicket.view.form;


import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public interface FlowConfirmModal<T extends PetitionEntity> extends Serializable {

    String getMarkup(String idSuffix);

    BSModalBorder init(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm);

}