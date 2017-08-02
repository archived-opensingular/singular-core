package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.lib.commons.report.ReportMetadata;

public class SingularFormReportMetadata<I extends SInstance> implements ReportMetadata<I> {
    private I filter = null;

    public SingularFormReportMetadata() {
    }

    public void setFilter(I filter) {
        if(this.filter != null){
            throw new SingularFormException("Já existe um filtro configurado");
        }
        this.filter = filter;
    }

    @Override
    public I getFilter() {
        return filter;
    }
}