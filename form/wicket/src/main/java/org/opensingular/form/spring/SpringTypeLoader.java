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

import org.opensingular.form.SType;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.RefTypeByKey;
import org.opensingular.form.document.TypeLoader;
import org.opensingular.internal.lib.support.spring.SpringUtils;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Optional;

/**
 * Loader de dicionário baseado no Spring. Espera que o mesmo será um bean do
 * Spring. Com isso cria referências  serializáveis mediante uso do nome do bean no Spring como forma de
 * recuperar o loader a partir da referência ao ser deserializada.
 *
 * @author Daniel C. Bordin
 */
public abstract class SpringTypeLoader<TYPE_KEY extends Serializable> extends TypeLoader<TYPE_KEY>
        implements ApplicationContextAware, BeanNameAware, NamedBean {

    private String springBeanName;

    @Override
    @Nonnull
    protected final Optional<RefType> loadRefTypeImpl(@Nonnull TYPE_KEY typeId) {
        return loadType(typeId).map(t -> new SpringRefType(SpringUtils.checkBeanName(this), typeId, t));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.setup(applicationContext);
    }

    @Override
    public void setBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    @Override
    public String getBeanName() {
        return springBeanName;
    }

    /** Representa um Referencia a SType que utiliza um bean Spring para resover a referencia apontada pelo ID. */
    final static class SpringRefType<KEY extends Serializable> extends RefTypeByKey<KEY> {

        private final String springBeanName;

        private SpringRefType(@Nonnull String springBeanName, @Nonnull KEY typeId, SType<?> type) {
            super(typeId, type);
            this.springBeanName = springBeanName;
        }

        @Override
        @Nonnull
        public SType<?> retrieveByKey(@Nonnull KEY typeId) {
            SpringTypeLoader<KEY> loader = ApplicationContextProvider.get().getBean(springBeanName, SpringTypeLoader.class);
            if (loader == null) {
                throw new SingularFormException(
                        "Não foi encontrado o bean de nome '" + springBeanName + "' do tipo " + SpringTypeLoader.class.getName());
            }
            return loader.loadType(typeId).orElseThrow(() -> new SingularFormException(
                    SpringUtils.erroMsg(loader, " não encontrou o " + SType.class.getSimpleName() + " para o id=" + typeId)));
        }

    }
}
