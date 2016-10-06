package org.opensingular.singular.form.wicket.mapper.masterdetail;

import org.opensingular.singular.form.wicket.mapper.components.MetronicPanel;


abstract class MasterDetailMetronicPanel extends MetronicPanel {

    MasterDetailMetronicPanel(String id) {
        super(id);
    }

    protected String getPanelWrapperClass() {
        return "list-detail-input";
    }

    protected String getPanelHeadingClass() {
        return "list-table-heading";
    }

    protected String getPanelBodyClass() {
        return "list-detail-body";
    }

    protected String getPanelFooterClass() {
        return "list-detail-footer";
    }

}