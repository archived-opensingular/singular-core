package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.wicket.views.SingularReportPanel;

public class SingularFormReportPanel<T extends SType<I>, I extends SInstance>
        extends SingularReportPanel<SingularFormReportMetadata<I>, I> {

    public SingularFormReportPanel(String id, SingularFormReport<T, I> singularFormReport) {
        super(id, () -> singularFormReport);
    }

    @Override
    protected SingularFormReportMetadata<I> makeReportMetadata() {
        return new SingularFormReportMetadata<>();
    }

}