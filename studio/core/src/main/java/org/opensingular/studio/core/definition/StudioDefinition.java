package org.opensingular.studio.core.definition;


import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.studio.core.panel.CrudEditContent;
import org.opensingular.studio.core.panel.CrudListContent;
import org.opensingular.studio.core.panel.CrudShellContent;
import org.opensingular.studio.core.panel.CrudShellManager;

import java.io.Serializable;

public interface StudioDefinition extends Serializable {

    Class<? extends FormRespository> getRepositoryClass();

    void configureStudioDataTable(StudioTableDefinition studioDataTable);

    String getTitle();

    default StudioCRUDPermissionStrategy getPermissionStrategy() {
        return StudioCRUDPermissionStrategy.ALL;
    }

    default FormRespository getRepository() {
        return ApplicationContextProvider.get().getBean(getRepositoryClass());
    }

    default CrudShellContent makeStartContent(CrudShellManager shellManager) {
        return makeListContent(shellManager);
    }

    default CrudEditContent makeEditContent(CrudShellManager crudShellManager, CrudShellContent previousContent, IModel<SInstance> instance) {
        return new CrudEditContent(crudShellManager, previousContent, instance);
    }

    default CrudListContent makeListContent(CrudShellManager shellManager) {
        return new CrudListContent(shellManager);
    }


}