/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.spring;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.RefTypeByKey;
import br.net.mirante.singular.form.document.TypeLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;
import java.util.Optional;

/**
 * Loader de dicionário baseado no Spring. Espera que o mesmo será um bean do
 * Spring. Com isso cria referências ({@link #createDictionaryRef(Serializable)}
 * ) serializáveis mediante uso do nome do bean no Spring como forma de
 * recuperar o loader a partir da referência ao ser deserialziada.
 *
 * @author Daniel C. Bordin
 */
public abstract class SpringTypeLoader<TYPE_KEY extends Serializable> extends TypeLoader<TYPE_KEY>
        implements ApplicationContextAware, BeanNameAware, NamedBean {

    private String springBeanName;

    @Override
    protected final Optional<RefType> loadRefTypeImpl(TYPE_KEY typeId) {
        Optional<SType<?>> type = loadType(typeId);
        if (type == null) {
            throw new SingularFormException(getClass().getName() + ".loadType(TYPE_KEY) retornou null em vez de um Optional");
        }
        return type.map(t -> new SpringRefType(SpringFormUtil.checkBeanName(this), typeId, t));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringFormUtil.setApplicationContext(applicationContext);
    }

    @Override
    public void setBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    @Override
    public String getBeanName() {
        return springBeanName;
    }

    final static class SpringRefType<KEY extends Serializable> extends RefTypeByKey<KEY> {

        private final String springBeanName;

        private SpringRefType(String springBeanName, KEY typeId, SType<?> type) {
            super(typeId, type);
            this.springBeanName = springBeanName;
        }

        @Override
        public SType<?> retrieveByKey(KEY typeId) {
            SpringTypeLoader<KEY> loader = SpringFormUtil.getApplicationContext().getBean(springBeanName, SpringTypeLoader.class);
            if (loader == null) {
                throw new SingularFormException(
                        "Não foi encontrado o bean de nome '" + springBeanName + "' do tipo " + SpringTypeLoader.class.getName());
            }
            return loader.loadType(typeId).orElseThrow(() -> new SingularFormException(
                    SpringFormUtil.erroMsg(loader, " não encontrou o " + SType.class.getSimpleName() + " para o id=" + typeId)));
        }

    }
}
