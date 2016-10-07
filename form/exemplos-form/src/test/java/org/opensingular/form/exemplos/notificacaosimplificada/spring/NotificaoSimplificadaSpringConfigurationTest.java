package org.opensingular.form.exemplos.notificacaosimplificada.spring;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class NotificaoSimplificadaSpringConfigurationTest {

    @Test
    public void testCreateAppContext() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(NotificaoSimplificadaSpringConfiguration.class);
        Assert.assertNotNull(context);
    }

}