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

import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implementação padrão da fábrica de documento para uso junto com o Spring.
 * Essa factory já tem a capacidade de integrar com o Spring para prover
 * implementações padrões do retornando o próprio
 * Spring, e do {@link #getDocumentFactoryRef()}, que retornar uma referência
 * que usurá o Spring para recuperar a própria fábrica.
 *
 * @author Daniel C. Bordin
 */
public abstract class SpringSDocumentFactory extends SDocumentFactory implements BeanNameAware, NamedBean {

    private String springBeanName;

    /**
     * Retorna um referência serializável à fábrica atual utilizando o nome do
     * bean registrado no spring para recuperar a fábrica após uma
     * deserialização.
     */
    @Override
    public RefSDocumentFactory createDocumentFactoryRef() {
        return new SpringRefSDocumentFactory(this);
    }

    @Override
    public String getBeanName() {
        return springBeanName;
    }

    @Override
    public void setBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }
}
