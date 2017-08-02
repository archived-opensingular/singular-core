package org.opensingular.lib.wicket.views;

import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.SingularReport;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;

public class BlankSingularReportPanel extends SingularReportPanel<ReportMetadata<Void>, Void> {
    public BlankSingularReportPanel(String id,
                                    ISupplier<SingularReport<ReportMetadata<Void>, Void>> singularReportSupplier) {
        super(id, singularReportSupplier);
    }

    @Override
    protected ReportMetadata<Void> makeReportMetadata() {
        return null;
    }
}
