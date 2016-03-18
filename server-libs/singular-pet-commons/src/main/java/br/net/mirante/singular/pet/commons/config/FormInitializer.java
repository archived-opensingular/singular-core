package br.net.mirante.singular.pet.commons.config;

import br.net.mirante.singular.form.spring.SpringSDocumentFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;

public abstract class FormInitializer {

    public abstract Class<? extends SpringSDocumentFactory> documentFactory();


    public void TypeLoader(){

    }

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        applicationContext.register(documentFactory());
    }
}
