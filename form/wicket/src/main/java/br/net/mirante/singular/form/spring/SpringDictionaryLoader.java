package br.net.mirante.singular.form.spring;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SDictionaryLoader;
import br.net.mirante.singular.form.mform.RefSDictionary;

/**
 * Loader de dicionário baseado no Spring. Espera que o mesmo será um bean do
 * Spring. Com isso cria referências ({@link #createDictionaryRef(Serializable)}
 * )serializáveis mediante uso do nome do bean no Spring como forma de recuperar
 * o loader a partir da referência ao ser deserialziada.
 *
 * @author Daniel C. Bordin
 */
public abstract class SpringDictionaryLoader<KEY extends Serializable> extends SDictionaryLoader<KEY>
        implements ApplicationContextAware, BeanNameAware, NamedBean {

    private String springBeanName;

    @Override
    protected RefSDictionary createDictionaryRef(KEY dictionaryId) {
        return new SpringRefSDictionary<KEY>(SpringFormUtil.checkBeanName(this), dictionaryId);
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

    final static class SpringRefSDictionary<KEY extends Serializable> extends RefSDictionary {

        private final String springBeanName;
        private final KEY dictionaryId;

        private SpringRefSDictionary(String springBeanName, KEY dictionaryId) {
            this.springBeanName = springBeanName;
            this.dictionaryId = dictionaryId;
        }

        @Override
        public SDictionary retrieve() {
            SDictionaryLoader<KEY> loader = SpringFormUtil.getApplicationContext().getBean(springBeanName, SDictionaryLoader.class);
            return loader.loadDictionaryOrException(dictionaryId);
        }

    }
}
