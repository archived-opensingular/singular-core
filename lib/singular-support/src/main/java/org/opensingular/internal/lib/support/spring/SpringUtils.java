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

package org.opensingular.internal.lib.support.spring;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.springframework.beans.factory.NamedBean;

import javax.annotation.Nonnull;

/**
 * Método utilitários para manipulação do Spring.
 *
 * @author Daniel C. Bordin on 23/05/2017.
 */
public class SpringUtils {

    private SpringUtils() {}

    /** Verificar se o nome do bean foi configurado. Ou seja, se o bean foi carregado via Spring. */
    @Nonnull
    public static String checkBeanName(@Nonnull NamedBean bean) {
        if (StringUtils.isBlank(bean.getBeanName())) {
            throw new SingularException(
                    "O nome do bean no spring não foi configurado para a classe " + bean.getClass().getName() +
                            ". Verifique se o bean foi corretamente registrado no Spring antes de ser utilizado.");
        }
        return bean.getBeanName();
    }

    /** Complementa a mensagem de erro com informações sobre o bean a que se refere. */
    public static String erroMsg(NamedBean bean, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        if (bean.getBeanName() != null) {
            sb.append("bean=").append(bean.getBeanName()).append("; ");
        }
        sb.append("class=").append(bean.getClass()).append("]: ").append(msg);
        return sb.toString();
    }

}
