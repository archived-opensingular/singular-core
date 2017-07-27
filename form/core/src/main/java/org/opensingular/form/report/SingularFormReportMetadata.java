package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.lib.commons.report.ReportMetadata;

public class SingularFormReportMetadata<I extends SInstance> implements ReportMetadata<I> {
    private final I filter;
    private final Boolean executeQuery;

    public SingularFormReportMetadata(I filter, Boolean executeQuery) {
        this.filter = filter;
        this.executeQuery = executeQuery;
    }

    @Override
    public I getFilter() {
        return filter;
    }

    public Boolean getExecuteQuery() {
        return executeQuery;
    }
}