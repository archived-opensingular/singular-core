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

package org.opensingular.form.spring;

import org.opensingular.form.SingularFormException;
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
