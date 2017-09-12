package org.opensingular.studio.core.definition;


import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.opensingular.form.SIComposite;
import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;

import java.io.Serializable;

public interface StudioDefinition extends Serializable {

    String getRepositoryBeanName();

    void configureDatatableColumns(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder);

    String getTitle();

    default StudioCRUDPermissionStrategy getPermissionStrategy() {
        return StudioCRUDPermissionStrategy.ALL;
    }
}