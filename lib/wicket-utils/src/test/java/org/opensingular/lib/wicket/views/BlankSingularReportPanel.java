package org.opensingular.lib.wicket.views;

import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportFilter;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;

public class BlankSingularReportPanel extends SingularReportPanel {
    public BlankSingularReportPanel(String id,
                                    ISupplier<SingularReport> singularReportSupplier) {
        super(id, singularReportSupplier);
    }

    @Override
    protected ReportMetadata<ReportFilter> makeReportMetadata() {
        return null;
    }
}
