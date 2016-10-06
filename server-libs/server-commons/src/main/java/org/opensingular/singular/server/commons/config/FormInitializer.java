package org.opensingular.singular.server.commons.config;

import org.opensingular.form.SType;
import org.opensingular.singular.form.spring.SpringSDocumentFactory;
import org.opensingular.singular.form.spring.SpringTypeLoader;
import org.opensingular.singular.server.commons.form.SingularServerDocumentFactory;
import org.opensingular.singular.server.commons.form.SingularServerFormConfigFactory;
import org.opensingular.singular.server.commons.form.SingularServerSpringTypeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.List;

public abstract class FormInitializer {

    public static final Logger logger        = LoggerFactory.getLogger(FormInitializer.class);
    static final        String SINGULAR_FORM = "[SINGULAR][FORM] %s";

    protected Class<? extends SpringSDocumentFactory> documentFactory() {
        return SingularServerDocumentFactory.class;
    }

    protected Class<? extends SpringTypeLoader> typeLoader() {
        return SingularServerSpringTypeLoader.class;
    }

    protected Class<?> formConfigFactory() {
        return SingularServerFormConfigFactory.class;
    }

    protected abstract List<Class<? extends SType<?>>> getTypes();

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
        if (formConfigFactory != null) {
            applicationContext.register(formConfigFactory);
        } else {
            logger.info(String.format(SINGULAR_FORM, " Null Form Config Factory, skipping Form Config Factory configuration. "));
        }
    }


}
