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

package org.opensingular.form.report;

import org.opensingular.form.SInstance;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.report.ReportFilter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormReportFilter implements ReportFilter {
    private final ISupplier<? extends SInstance> instanceSupplier;
    private final Map<String, Serializable> parameters;

    public FormReportFilter(ISupplier<? extends SInstance> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
        this.parameters = new LinkedHashMap<>();
    }

    @Override
    public void load(String XML) {
        SFormXMLUtil.fromXML(instanceSupplier.get(), XML);
    }

    @Override
    public String dumpXML() {
        return SFormXMLUtil.toStringXMLOrEmptyXML(instanceSupplier.get());
    }

    @Override
    public void setParam(String key, Serializable val) {
        parameters.put(key, val);
    }

    @Override
    public Object getParam(String key) {
        return parameters.get(key);
    }

    public SInstance getInstance() {
        return instanceSupplier.get();
    }
}