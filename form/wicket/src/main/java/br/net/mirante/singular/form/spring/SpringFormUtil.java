package br.net.mirante.singular.form.spring;

import org.springframework.context.ApplicationContext;

import br.net.mirante.singular.form.mform.SingularFormException;

/**
 * Guarda um referência estática para o contexto de aplicação Spring de forma a
 * permitir o primeiro bean que receber o contexto da aplicação deixe disponível
 * para todos os demais.
 *
 * @author Daniel C. Bordin
 */
class SpringFormUtil {

    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext ctx) {
        if (ctx != null) {
            applicationContext = ctx;
        }
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new SingularFormException("ApplicationContext ainda não foi configurado (null)");
        }
        return applicationContext;
    }
}
