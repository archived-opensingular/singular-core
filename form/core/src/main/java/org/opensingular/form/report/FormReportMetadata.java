package org.opensingular.form.report;

import org.opensingular.lib.commons.report.ReportMetadata;

public class FormReportMetadata implements ReportMetadata<SingularFormReportFilter> {
    private SingularFormReportFilter filter = null;

    public FormReportMetadata() {
    }

    @Override
    public void setFilter(SingularFormReportFilter filter) {
        this.filter = filter;
    }

    @Override
    public SingularFormReportFilter getFilter() {
        return filter;
    }
}