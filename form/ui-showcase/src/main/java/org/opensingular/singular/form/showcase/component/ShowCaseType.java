/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.singular.form.showcase.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public enum ShowCaseType {
    FORM,
    STUDIO;

    public static final String SHOWCASE_TYPE_PARAM = "tp";


    public static PageParameters buildPageParameters(ShowCaseType showCaseType){
        return buildPageParameters(null, showCaseType);
    }

    public static PageParameters buildPageParameters(String componentName){
        return buildPageParameters(componentName, null);
    }

    public static PageParameters buildPageParameters(String componentName, ShowCaseType showCaseType){
        final PageParameters pageParameters = new PageParameters();
        if (showCaseType == null) {
            final StringValue tipo = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(SHOWCASE_TYPE_PARAM);
            if (!tipo.isNull()) {
                pageParameters.add(ShowCaseType.SHOWCASE_TYPE_PARAM, tipo);
            }
        } else {
            pageParameters.add(ShowCaseType.SHOWCASE_TYPE_PARAM, showCaseType.name());
        }

        if (StringUtils.isNotEmpty(componentName)) {
            pageParameters.add("cn", componentName);
        }
        return pageParameters;
    }
}
