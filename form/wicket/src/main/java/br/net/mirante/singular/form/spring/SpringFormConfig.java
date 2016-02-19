package br.net.mirante.singular.form.spring;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NamedBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.net.mirante.singular.form.mform.SDictionaryLoader;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;

/**
 * Representa a configuração para funcionamento do formulário.
 *
 * @author Daniel C. Bordin
 */
public class SpringFormConfig<KEY extends Serializable> implements SFormConfig<KEY>, ApplicationContextAware, BeanNameAware, NamedBean {

    private String springBeanName;

    private SDocumentFactory documentFactory;

    private SDictionaryLoader<KEY> dictionaryLoader;

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
    public SDictionaryLoader<KEY> getDictionaryLoader() {
        if (dictionaryLoader == null) {
            throw new SingularFormException(SpringFormUtil.erroMsg(this, "O atributo dictionaryLoader não foi configurado"));
        }
        return dictionaryLoader;
    }

    public void setDictionaryLoader(SDictionaryLoader<KEY> dictionaryLoader) {
        this.dictionaryLoader = dictionaryLoader;
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
