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
 * It Represents both an report definition and a report instance.
 *
 * @param <F> filter parametric type
 */
public interface SingularReport<F> extends Serializable {

    String getReportName();

    /**
     * the view generator to build the reports
     *
     * @return the viewgenerator
     */
    ViewGenerator getViewGenerator();

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

    default String getIdentity() {
        return SingularUtil.convertToJavaIdentity(getReportName(), true);
    }


    /**
     * Loads the XML of an Report Instance
     *
     * @param XML the content
     */
    void loadReportInstance(String XML);

    /**
     * Extract the XML definition of this Report Instance
     *
     * @return the content
     */
    String dumpReportInstanceXML();

    /**
     * Set parameters
     *
     * @param key the param key
     * @param val the param value
     */
    void setParam(String key, Serializable val);

    /**
     * Get the value of a parameter
     *
     * @param key the param key
     * @return the value
     */
    Serializable getParam(String key);

    /**
     *
     * @return
     *  current instance object value
     */
    F getFilterValue();

    /**
     * uptates the internal state of the filter value
     * @param value
     */
    void setFilterValue(F value);
}