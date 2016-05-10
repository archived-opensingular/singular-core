package br.net.mirante.singular.form.provider;


import br.net.mirante.singular.form.SingularFormException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.apache.commons.lang3.StringUtils;
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

    public static String mergeWithFreemarker(String template, Object obj) {
        if (obj == null) {
            return StringUtils.EMPTY;
        }
        final StringWriter sw = new StringWriter();
        try {
            new Template(String.valueOf(template.hashCode()), new StringReader(template), cfg).process(obj, sw);
        } catch (IOException | TemplateException e) {
            LOGGER.error(e.getMessage(), e);
            throw new SingularFormException("Não foi possivel fazer o merge do template " + template);
        }
        return sw.toString();
    }

}