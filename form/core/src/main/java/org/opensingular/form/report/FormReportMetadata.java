package org.opensingular.form.report;

import org.opensingular.lib.commons.report.ReportMetadata;

public class FormReportMetadata implements ReportMetadata<FormReportFilter> {
    private FormReportFilter filter;

    FormReportMetadata() {
        filter = null;
    }

    @Override
    public void setFilter(FormReportFilter filter) {
        this.filter = filter;
    }


    @Override
    public FormReportFilter getFilter() {
        return filter;
    }
}