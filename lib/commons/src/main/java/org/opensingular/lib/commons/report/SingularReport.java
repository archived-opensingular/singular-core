package org.opensingular.lib.commons.report;

import org.opensingular.lib.commons.views.ViewGenerator;


/**
 * Singular Report
 * @param <R> the report metadata type
 * @param <T> the filter type
 */
public interface SingularReport<R extends ReportMetadata<T>, T> {

    /**
     * the view generator to build the reports
     * @return the viewgenerator
     */
    ViewGenerator makeViewGenerator(R reportMetadata);

}