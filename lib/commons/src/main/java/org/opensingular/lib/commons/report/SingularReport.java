package org.opensingular.lib.commons.report;

import org.opensingular.lib.commons.views.ViewGenerator;


/**
 * Singular Report
 * @param <R> the report metadata type
 * @param <T> the filter type
 */
public interface SingularReport<R extends ReportMetadata<T>, T> {
    /**
     * The Report Name
     * @return the name
     */
    String getReportName();

    /**
     * the view generator to build the reports
     * @return the viewgenerator
     */
    ViewGenerator makeViewGenerator(R reportMetadata);

    /**
     * Informa se o relatorio deve ser carregado automaticamente ou somente apos
     * uma iteração do usuario (Somente para relatorios manuais)
     * @return se deve ser carregado automaticamente
     */
    default Boolean eagerLoading(){
        return Boolean.FALSE;
    }
}