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

package org.opensingular.server.p.commons;


import org.opensingular.form.SingularFormException;
import org.opensingular.form.provider.FreemarkerUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import java.io.StringWriter;
import java.util.*;

import static freemarker.template.Configuration.VERSION_2_3_22;

public class PServerFreeMarkerUtil {

    private static final Version       VERSION = VERSION_2_3_22;
    private static final Configuration cfg     = new Configuration(VERSION);
    private static final Logger        LOGGER  = LoggerFactory.getLogger(FreemarkerUtil.class);

    static {
        final BeansWrapper wrapper = new BeansWrapperBuilder(VERSION).build();
        cfg.setObjectWrapper(wrapper);
        cfg.setTemplateLoader(new ClassTemplateLoader(PServerFreeMarkerUtil.class.getClassLoader(), "templates"));
        cfg.setDefaultEncoding("UTF-8");
    }

    public static String mergeWithFreemarker(String templateName, Map<String, Object> model) {

        if (model == null || templateName == null) {
            return StringUtils.EMPTY;
        }

        final StringWriter sw  = new StringWriter();
        final Map          map = new ObjectMapper().convertValue(model, Map.class);

        try {
            cfg.getTemplate(templateName).process(encode(map), sw);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new SingularFormException("Não foi possivel fazer o merge do template " + templateName);
        }

        return sw.toString();
    }

    private static Object encode(Object o) {
        final Map m = new HashMap();
        if (o instanceof Map) {
            ((Map) o).forEach((k, v) -> {
                m.put(k, encode(v));
            });
        } else if (o instanceof String) {
            return HtmlUtils.htmlEscape((String) o);
        } else if (o instanceof Collection) {
            List<Object> list = new ArrayList<>();
            ((Collection) o).forEach(x -> list.add(encode(x)));
            return list;
        } else {
            return o;
        }
        return m;
    }

}
