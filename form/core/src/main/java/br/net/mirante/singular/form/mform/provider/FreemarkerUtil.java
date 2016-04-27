package br.net.mirante.singular.form.mform.provider;


import br.net.mirante.singular.form.mform.SingularFormException;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static freemarker.template.Configuration.VERSION_2_3_22;

public class FreemarkerUtil {

    private static final Version       VERSION          = VERSION_2_3_22;
    private static final Configuration cfg              = new Configuration(VERSION);
    private static final Logger        LOGGER           = LoggerFactory.getLogger(FreemarkerUtil.class);

    static {
        final DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(VERSION);
        cfg.setObjectWrapper(builder.build());
    }

    public static String mergeWithFreemarker(String template, Object obj) {
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