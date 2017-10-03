package org.opensingular.form.report.extension;

import org.opensingular.form.report.ReportPage;
import org.opensingular.lib.commons.extension.SingularExtension;

public interface ReportMenuExtension extends SingularExtension {

    void configure(ReportPage.ReportMenuBuilder reportMenuBuilder);

}