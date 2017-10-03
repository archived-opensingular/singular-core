package org.opensingular.form.report;

import org.opensingular.form.SType;
import org.opensingular.lib.commons.report.SingularReport;

/**
 * SingularFormReport
 * <p>
 * Interface for create reports using the SingularForms engine
 *
 */
public interface SingularFormReport extends SingularReport<FormReportMetadata, FormReportFilter> {
    /**
     * The type to build the filter
     * @return the type
     */
    Class<? extends SType<?>> getFilterType();
}