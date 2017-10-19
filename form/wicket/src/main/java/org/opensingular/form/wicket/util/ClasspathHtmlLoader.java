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

package org.opensingular.form.wicket.util;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.SingularFormException;
import org.opensingular.lib.wicket.util.util.JavaScriptUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClasspathHtmlLoader {

    private String   name;
    private Class<?> scope;

    public ClasspathHtmlLoader(String name, Class<?> scope) {
        this.name = name;
        this.scope = scope;
    }

    public String loadHtml() {
        InputStream htmlInputStream = scope.getResourceAsStream(name);
        if (htmlInputStream != null) {
            return javascriptEscape(htmlInputStream);
        }
        return null;
    }

    private String javascriptEscape(InputStream htmlInputStream) {
        try {
            return JavaScriptUtils.javaScriptEscape(IOUtils.toString(htmlInputStream, StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            throw new SingularFormException("Não foi possivel extrair o conteudo html", e);
        }
    }

}