package org.opensingular.lib.commons.report;

import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewOutputFormat;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


/**
 * Singular Report
 *
 * @param <R> the report metadata type
 * @param <T> the filter type
 */
public interface SingularReport<R extends ReportMetadata<T>, T extends ReportFilter> extends Serializable {
    /**
     * The Report Name
     *
     * @return the name
     */
    String getReportName();

    /**
     * the view generator to build the reports
     *
     * @return the viewgenerator
     */
    ViewGenerator makeViewGenerator(R reportMetadata);

    /**
     * Informa se o relatorio deve ser carregado automaticamente ou somente apos
     * uma iteração do usuario (Somente para relatorios manuais)
     *
     * @return se deve ser carregado automaticamente
     */
    default Boolean eagerLoading() {
        return Boolean.FALSE;
    }

    /**
     * Retorna os tipos de arquivo habilitados para exportação
     *
     * @return uma lista de {@link ViewOutputFormat}
     */
    default List<ViewOutputFormat> getEnabledExportFormats() {
        return Arrays.asList(ViewOutputFormat.HTML, ViewOutputFormat.EXCEL, ViewOutputFormat.HTML);
    }

    default void onFilterInit(T filter){

    }

    default String getIdentity(){
        return SingularUtil.convertToJavaIdentity(getReportName(), true);
    }
}