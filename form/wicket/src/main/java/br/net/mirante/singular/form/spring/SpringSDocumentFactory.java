package br.net.mirante.singular.form.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.SDocumentFactoryRef;
import br.net.mirante.singular.form.mform.document.ServiceRegistry;

/**
 * Implementação padrão da fábrica de documento para uso junto com o Spring.
 * Essa factory já tem a capacidade de integrar com o Spring para prover
 * implementações padrões do {@link #getServiceRegistry()}, retornando o próprio
 * Spring, e do {@link #getDocumentFactoryRef()}, que retornar uma referência
 * que usurá o Spring para recuperar a própria fábrica.
 *
 * @author Daniel C. Bordin
 */
public abstract class SpringSDocumentFactory extends SDocumentFactory implements ApplicationContextAware, BeanNameAware {

    private String springBeanName;

    private ServiceRegistry serviceRegistry;

    /**
     * Retorna como registro de serviço um proxy para o próprio
     * ApplicationContext do Spring.
     */
    @Override
    public ServiceRegistry getServiceRegistry() {
        if (serviceRegistry == null) {
            serviceRegistry = new SpringServiceRegistry(SpringFormUtil.getApplicationContext());
        }
        return serviceRegistry;
    }

    /**
     * Retorna um referência serializável à fábrica atual utilizando o nome do
     * bean registrado no spring para recuperar a fábrica após uma
     * deserialização.
     */
    @Override
    public SDocumentFactoryRef getDocumentFactoryRef() {
        if (springBeanName == null) {
            throw new SingularFormException("O nome do bean não foi configurado");
        }
        return new SpringSDocumentFactoryRef(springBeanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringFormUtil.setApplicationContext(applicationContext);
    }

    @Override
    public void setBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }
}