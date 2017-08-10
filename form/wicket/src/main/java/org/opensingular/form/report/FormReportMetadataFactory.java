package org.opensingular.form.report;

import org.opensingular.lib.commons.report.ReportMetadata;
import org.opensingular.lib.commons.report.ReportMetadataFactory;

public class FormReportMetadataFactory implements ReportMetadataFactory {
    @Override
    public ReportMetadata get() {
        return new FormReportMetadata();
    }
}