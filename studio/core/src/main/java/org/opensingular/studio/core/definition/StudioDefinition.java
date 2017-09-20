package org.opensingular.studio.core.definition;


import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;

import java.io.Serializable;
import java.util.LinkedHashMap;

public interface StudioDefinition extends Serializable {

    Class<? extends FormRespository> getRepositoryClass();

    void configureStudioDataTable(StudioDataTable studioDataTable);

    String getTitle();

    default StudioCRUDPermissionStrategy getPermissionStrategy() {
        return StudioCRUDPermissionStrategy.ALL;
    }

    default FormRespository getRepository(){
        return ApplicationContextProvider.get().getBean(getRepositoryClass());
    }

    class StudioDataTable {
        private LinkedHashMap<String, String> columns = new LinkedHashMap<>();

        public void add(String columnName, String path) {
            columns.put(columnName, path);
        }

        public LinkedHashMap<String, String> getColumns() {
            return columns;
        }
    }

}