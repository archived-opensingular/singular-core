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

package org.opensingular.form.wicket.mapper.richtext;

import org.opensingular.form.wicket.util.ClasspathHtmlLoader;
import org.springframework.util.StringUtils;

public class RichTextNewTabHtml {

    private static final String BASE_URL_PLACEHOLDER = "#BASE_URL_PLACEHOLDER#";
    private static final String CSS_PLACEHOLDER = "CSS_PLACEHOLDER";

    private ClasspathHtmlLoader classpathHtmlLoader = new ClasspathHtmlLoader("PortletRichTextNewTab.html", this.getClass());
    private String loadedHtml = null;
    private StringBuilder buttonsCssStyle = new StringBuilder();
    private String baseurl;

    public RichTextNewTabHtml(String baseurl, String listaIds) {
        this.baseurl = baseurl;
        for (String id : listaIds.split(",")) {
            buttonsCssStyle.append("        .cke_button__").append(id.toLowerCase()).append("_label, ");
        }
        buttonsCssStyle.deleteCharAt(buttonsCssStyle.length()-1);
        buttonsCssStyle.deleteCharAt(buttonsCssStyle.length()-1);

    }

    public String retrieveHtml() {
        if (loadedHtml == null) {
            loadedHtml = classpathHtmlLoader.loadHtml();
        }

        if(StringUtils.isEmpty(buttonsCssStyle)){
            buttonsCssStyle.append(".noClassForButton ");
        }
        loadedHtml = loadedHtml.replace(CSS_PLACEHOLDER, buttonsCssStyle);
        return loadedHtml.replace(BASE_URL_PLACEHOLDER, baseurl);
    }

}
