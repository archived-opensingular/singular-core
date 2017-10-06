package org.opensingular.studio.core.definition;


import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;

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

}