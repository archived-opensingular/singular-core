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

import java.io.Serializable;

public interface ReportFilter extends Serializable {

    /**
     * Load the XML into the filter
     *
     * @param XML the content
     */
    void load(String XML);

    /**
     * Extract XML content
     *
     * @return the content
     */
    String dumpXML();

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
    Object getParam(String key);
}