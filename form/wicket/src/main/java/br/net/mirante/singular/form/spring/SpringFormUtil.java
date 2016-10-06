/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.spring;

import org.opensingular.singular.form.SingularFormException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;

/**
 * Guarda um referência estática para o contexto de aplicação Spring de forma a
 * permitir o primeiro bean que receber o contexto da aplicação deixe disponível
 * para todos os demais.
 *
 * @author Daniel C. Bordin
 */
class SpringFormUtil {

    private static volatile ApplicationContext applicationContext;

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

    /**
     * Verificar se o nome do bean foi configurado. Ou seja, se o bean foi
     * carregado via Spring.
     */
    public static String checkBeanName(NamedBean bean) {
        if (StringUtils.isBlank(bean.getBeanName())) {
            throw new SingularFormException("O nome do bean no spring não foi configurado para a classe " + bean.getClass().getName()
                    + ". Verifique se o bean foi corretamente registrado no Spring antes de ser utilizado.");
        }
        return bean.getBeanName();
    }

    public static String erroMsg(NamedBean bean, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (bean.getBeanName() != null) {
            sb.append("bean=").append(bean.getBeanName()).append("; ");
        }
        sb.append("class=").append(bean.getClass()).append("]: ").append(msg);
        return msg.toString();
    }
}
