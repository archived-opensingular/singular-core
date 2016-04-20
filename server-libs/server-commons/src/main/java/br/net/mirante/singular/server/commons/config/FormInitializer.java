package br.net.mirante.singular.server.commons.config;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.spring.SpringSDocumentFactory;
import br.net.mirante.singular.form.spring.SpringTypeLoader;
import br.net.mirante.singular.persistence.util.HibernateSingularFlowConfigurationBean;
import br.net.mirante.singular.server.commons.form.SingularServerDocumentFactory;
import br.net.mirante.singular.server.commons.form.SingularServerFormConfigFactory;
import br.net.mirante.singular.server.commons.form.SingularServerSpringTypeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Map;

public abstract class FormInitializer {

    public static final Logger logger = LoggerFactory.getLogger(FormInitializer.class);
    static final String SINGULAR_FORM = "[SINGULAR][FORM] %s";

    protected Class<? extends SpringSDocumentFactory> documentFactory(){
        return SingularServerDocumentFactory.class;
    }

    protected Class<? extends SpringTypeLoader> typeLoader(){
        return SingularServerSpringTypeLoader.class;
    }

    protected Class<?> formConfigFactory(){
        return SingularServerFormConfigFactory.class;
    }

    protected abstract Map<Class<? extends SPackage>, String> formPackagesMap();

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        Class<?> documentFactory = documentFactory();
        if (documentFactory != null) {
            applicationContext.register(documentFactory);
        } else {
            logger.info(String.format(SINGULAR_FORM, " Null Form Document Factory, skipping Form Document Factory configuration. "));
        }
        Class<?> typeLoader = typeLoader();
        if (typeLoader != null) {
            applicationContext.register(typeLoader);
        } else {
            logger.info(String.format(SINGULAR_FORM, " Null Form Type Loader, skipping Form Type Loader configuration. "));
        }
        Class<?> formConfigFactory = formConfigFactory();
        if (typeLoader != null) {
            applicationContext.register(formConfigFactory);
        } else {
            logger.info(String.format(SINGULAR_FORM, " Null Form Config Factory, skipping Form Config Factory configuration. "));
        }
    }


}
