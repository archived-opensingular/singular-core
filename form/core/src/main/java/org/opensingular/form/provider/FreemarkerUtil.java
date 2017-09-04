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

package org.opensingular.form.provider;


import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SingularFormException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static freemarker.template.Configuration.VERSION_2_3_22;

public class FreemarkerUtil {

    private static final Version       VERSION = VERSION_2_3_22;
    private static final Configuration cfg     = new Configuration(VERSION);
    private static final Logger        LOGGER  = LoggerFactory.getLogger(FreemarkerUtil.class);

    static {
        final BeansWrapper wrapper = new BeansWrapperBuilder(VERSION).build();
        cfg.setObjectWrapper(wrapper);
    }

    private FreemarkerUtil() {}

    public static String mergeWithFreemarker(String template, Object obj) {

        if (obj == null || template == null) {
            return StringUtils.EMPTY;
        }

        StringWriter sw = new StringWriter();

        try {
            new Template(String.valueOf(template.hashCode()), new StringReader(safeWrap(template)), cfg).process(obj, sw);
        } catch (IOException | TemplateException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SingularFormException("Não foi possivel fazer o merge do template " + template, e);
        }

        return sw.toString();
    }

    public static String safeWrap(String template) {
        return template
                .replaceAll("\\$\\{", "\\${(")
                .replaceAll("}", ")!''}");
    }

}