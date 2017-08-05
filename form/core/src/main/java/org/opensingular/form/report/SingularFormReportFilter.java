package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportFilter;

public class SingularFormReportFilter implements ReportFilter {
    private final ISupplier<? extends SInstance> instanceSupplier;

    public SingularFormReportFilter(ISupplier<? extends SInstance> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Override
    public void load(String XML) {
        SFormXMLUtil.fromXML(instanceSupplier.get(), XML);
    }

    @Override
    public String dumpXML() {
        return SFormXMLUtil.toStringXMLOrEmptyXML(instanceSupplier.get());
    }

    public SInstance getInstance() {
        return instanceSupplier.get();
    }
}