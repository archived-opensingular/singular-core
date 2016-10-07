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
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.TypeLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;

/**
 * Representa a configuração para funcionamento do formulário.
 *
 * @author Daniel C. Bordin
 */
public class SpringFormConfig<KEY extends Serializable> implements SFormConfig<KEY>, ApplicationContextAware, BeanNameAware, NamedBean {

    private String springBeanName;

    private SDocumentFactory documentFactory;

    private TypeLoader<KEY> typeLoader;

    @Override
    public SDocumentFactory getDocumentFactory() {
        if (documentFactory == null) {
            return new SpringSDocumentFactoryEmpty();
        }
        return documentFactory;
    }

    public void setDocumentFactory(SDocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }

    @Override
    public TypeLoader<KEY> getTypeLoader() {
        if (typeLoader == null) {
            throw new SingularFormException(SpringFormUtil.erroMsg(this, "O atributo typeLoader não foi configurado"));
        }
        return typeLoader;
    }

    public void setTypeLoader(TypeLoader<KEY> typeLoader) {
        this.typeLoader = typeLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringFormUtil.setApplicationContext(applicationContext);
    }

    @Override
    public void setBeanName(String name) {
        this.springBeanName = name;
    }

    @Override
    public String getBeanName() {
        return springBeanName;
    }
}
