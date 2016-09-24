package br.net.mirante.singular.server.p.commons;


import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.provider.FreemarkerUtil;
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
import java.util.HashMap;
import java.util.Map;

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

    private static Map encode(Map dirty) {
        final Map m = new HashMap();
        dirty.forEach((k, v) -> {
            if (v instanceof Map) {
                m.put(k, encode((Map) v));
            } else if (v instanceof String) {
                m.put(k, HtmlUtils.htmlEscape((String) v));
            } else {
                m.put(k, v);
            }
        });
        return m;
    }


}
