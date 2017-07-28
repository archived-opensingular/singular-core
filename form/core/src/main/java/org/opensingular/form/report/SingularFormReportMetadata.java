package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.report.ReportMetadata;

public class SingularFormReportMetadata<I extends SInstance> implements ReportMetadata<I> {
    private final I filter;

    public SingularFormReportMetadata(I filter) {
        this.filter = filter;
    }

    @Override
    public I getFilter() {
        return filter;
    }
}