/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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